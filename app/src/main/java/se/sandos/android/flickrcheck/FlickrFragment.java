package se.sandos.android.flickrcheck;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Deque;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import se.sandos.android.flickrcheck.json.PhotoInfo;
import se.sandos.android.flickrcheck.json.PhotoInfo.Photo.Tag;
import se.sandos.android.flickrcheck.json.PhotoSearch.Photos;
import se.sandos.android.flickrcheck.json.PhotoSearch.Photos.Photo;
import se.sandos.flickrcheck.R;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import android.content.ContentUris;

public class FlickrFragment extends Fragment {
	
	private FlickrApi api;
	
	private TextView status;
	private TextView size;
	
	private File currentFile;
	
	private Set<File> skipped;
	
	private View rootView;
	
	private boolean videos = false;
	
	private VideoView localVideo;
	private VideoView remoteVideo;

	private String location;

	private ArrayList<Uri> toDelete;

	final AsyncTask<String, Integer, String> at = new AsyncTask<String, Integer, String>(){
		@Override
		protected String doInBackground(String... params) {
			Log.w(MainActivity.LOG_TAG, "Getting tokens");
			Intent login;
			try {
				setStatus("Begin");
				login = api.login();
				
				if(login != null) {
					setStatus("Sent user");
					startActivity(login);
				} else {
					setStatus("NULL login?");
				}
			} catch (Exception e) {
				Log.w(MainActivity.LOG_TAG, e);
				setStatus("Failed login");
			}
			
			
	        return "<No>";
		}
		
		protected void onPostExecute(String result) {
			TextView hello = (TextView) rootView.findViewById(R.id.hello);
			hello.setText(result);
		}
	};
	
	public FlickrFragment() {
		api = new FlickrApi();
		skipped = new HashSet<File>();
		toDelete = new ArrayList<Uri>();
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public void setVideo(boolean video) {
		videos = video;
	}
	
	public boolean isVideos() {
		return videos;
	}
	
	public FlickrApi getApi() {
		return api;
	}
	
    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
	
	public class FetchNextPhoto extends AsyncTask<String, Integer, String> {
		private Bitmap remote;
		private Bitmap local;
		
		private boolean digestMatch;
		private String digest;
		@Override
		protected String doInBackground(String... params) {
			try {
				File biggestFolder = null;
				if(location == null) {
					long biggest = -1;
					for (File f : getAlbumStorageDir().listFiles()) {
						long totSize = 0;
						for(File ff : f.listFiles())
						{
							totSize += ff.length();
						}
						Log.w(MainActivity.LOG_TAG, "Size of files in " + f + " is " + totSize);
						if(totSize > biggest) {
							biggest = totSize;
							biggestFolder = f;
						}
					}
				} else {
					biggestFolder = new File(location);
				}

				if(biggestFolder != null) {
					setStatus("Folder " + biggestFolder.getName());
					long biggestSize = -1;
					File biggestFile = null;
					String ending = "jpg";
					if(videos) {
						ending = "mp4";
					}
					int numFiles = 0;
					int totalFiles = 0;
					for(File f : biggestFolder.listFiles()) {
						totalFiles++;
						if(!skipped.contains(f) && f.getName().endsWith(ending)){
							numFiles++;
							if(biggestSize < f.length()) {
								biggestFile = f;
								biggestSize = f.length();
								Log.w(MainActivity.LOG_TAG, "New biggest file: " + f.getName() + "|" + f.length());
							}
						}
					}

					if(biggestFile != null) {
						final File f = biggestFile;

						setStatus("File " + biggestFile.getName() + " Num:" + numFiles);
						currentFile = biggestFile;
						setSize(biggestSize);
						
						if(!videos) {
							BitmapFactory.Options options = new BitmapFactory.Options();
							options.inSampleSize = 8;
							local = BitmapFactory.decodeStream(new FileInputStream(biggestFile), null, options);
							local = ExifUtil.rotateBitmap(biggestFile.getAbsolutePath(), local);
							Log.w(MainActivity.LOG_TAG, "Local size: " + local.getHeight());

							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									VideoView video = (VideoView) rootView.findViewById(R.id.localVideo);
									video.setVisibility(VideoView.GONE);
									video = (VideoView) rootView.findViewById(R.id.remoteVideo);
									video.setVisibility(VideoView.GONE);

									ImageView img = (ImageView) rootView.findViewById(R.id.localImage);
									img.setImageBitmap(local);
								}
							});
						} else {
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if(localVideo != null) {
										Log.w(MainActivity.LOG_TAG, "Clearing local video");
										localVideo.stopPlayback();
										localVideo.setVisibility(VideoView.GONE);
									}
									if(remoteVideo != null) {
										Log.w(MainActivity.LOG_TAG, "Clearing remote video");
										remoteVideo.stopPlayback();
										remoteVideo.setVisibility(VideoView.GONE);
									}

									VideoView video = (VideoView) rootView.findViewById(R.id.localVideo);
									video.setVisibility(VideoView.VISIBLE);
									video.setVideoPath(Uri.fromFile(f).toString());
									
									MediaController mc = new MediaController(getActivity().getApplicationContext());
									mc.setMediaPlayer(video);
									video.requestFocus();
//									video.start();
									localVideo = video;
								}
							});
						}
						
						
						Log.w(MainActivity.LOG_TAG, "Biggest file: " + biggestFile);
						final String title = biggestFile.getName();
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								getActivity().setTitle(title);
							}
						});
						String n = biggestFile.getName();
						if(api == null) {
							setStatus("API null");
							return "";
						}
						
						final Photos search = api.search(n.substring(0, n.length()-4));
						digestMatch = false;
						if(search != null && search.getTotal() > 0) {
							String foundSha1 = "";
							for(Photo ph : search.photo) {
								PhotoInfo info = api.getPhotoInfo(ph.id);
								
								for(Tag g : info.photo.tags.tag) {
									if(g.raw.startsWith("file:md5sum=")) {
										if(foundSha1 != null) {
											Log.w(MainActivity.LOG_TAG, "Found more than one SHA1");
										}
										foundSha1 = g.raw.substring(12, g.raw.length());
									}
								}
							}
							final String remotesha = foundSha1;
							Log.w("majs", "Digest stuff " + remotesha + "|" + digest);
							
							digest = null;
							setStatus("Search found " + search.getTotal());
							URL u = new URL(search.photo.get(0).baseUrl("_n"));

							Thread digestThread = new Thread(new Runnable() {
								@Override
								public void run() {
									FileInputStream fis = null;
									String ourRemoteDigest = remotesha;
									try {
										int remoteTotal = 0;
										ByteArrayOutputStream remoteFile = new ByteArrayOutputStream();
										if(remotesha == null || remotesha.isEmpty()) {
											final String origUrl = api.getOrigImgUrl(search.photo.get(0).getId());
											InputStream is = new URL(origUrl).openConnection().getInputStream();
											Log.w("majs", "Downloading file to compute digest! " + origUrl);
											//We need to compute the hash manually
											MessageDigest md = MessageDigest.getInstance("MD5");
											byte[] buffer = new byte[1024*512];
											int read;
											while ((read = is.read(buffer)) != -1) {
												remoteFile.write(buffer, 0, read);
												md.update(buffer, 0, read);
												remoteTotal += read;
											}
											byte[] sha1 = md.digest();
											ourRemoteDigest = convertToHex(sha1);
											Log.w("majs", "Downloaded file to compute digest! " + ourRemoteDigest + " Size: " + remoteTotal);
										}
										//Compute SHA1
										byte[] remoteBytes = remoteFile.toByteArray();
										Log.w("majs", "start hashing");
										MessageDigest md = MessageDigest.getInstance("MD5");
										fis = new FileInputStream(f);
										byte[] buffer = new byte[1024*512];
										int read;
										int total = 0;
										ArrayList<Integer> diff = new ArrayList<>(10000);
										int totalDiff = 0;
										while ((read = fis.read(buffer)) != -1) {
											md.update(buffer, 0, read);
											//Compare bytes
											diff.clear();
											for(int index =  total; index < total + read; index++){
												if(remoteBytes[index] != buffer[index - total]) {
													diff.add(index);
													totalDiff++;
//													Log.w("majs", "Diff " + index + " " + remoteBytes[index] + ":" + buffer[index-total]);
												}
											}
											if(diff.size() > 0 ) {
//												Log.w("majs", "Number of differing bytes: " + diff.size() + " Index: " + total);
											}
											total += read;
										}
										byte[] sha1 = md.digest();
										Log.w("majs", "done hashing");
										digest = convertToHex(sha1);
										digestMatch = ourRemoteDigest.equals(digest);
										Log.w("majs", "Digests: " + digest + "[" + total + "]:" + ourRemoteDigest + " [" + remoteTotal + "]");
										if(!digestMatch && remoteTotal == total && totalDiff < 100) {
											digestMatch = true;
											Log.w("majs", "Setting match to true due to few diffing bytes: " + totalDiff);
											setStatus("Setting match to true due to few diffing bytes: " + totalDiff);
										} else {
											Log.w("majs", "Not setting match: " + remoteTotal + " Local: " + total);
										}
										if (digestMatch) {
											getActivity().runOnUiThread(new Runnable() {
												@Override
												public void run() {
													Button deleteBtn = (Button) rootView.findViewById(R.id.deleteBtn);
													if (digestMatch) {
														deleteBtn.setBackgroundColor(0xff00dd00);
													} else {
														deleteBtn.setBackgroundResource(android.R.drawable.btn_default);
													}

													deleteBtn.callOnClick();
												}
											});
										}
									} catch(Throwable e) {
										Log.w(MainActivity.LOG_TAG, e);
									} finally {
										if(fis != null) {
											try {
												fis.close();
											} catch (IOException e) {
												Log.w(MainActivity.LOG_TAG, e);
											}
										}
									}
								}
							});

							digestThread.setName("MD5 thread");
							digestThread.setPriority(Thread.MIN_PRIORITY);
							digestThread.start();

							if(videos) {
								final String videoUrl = api.getVideoUrl(search.photo.get(0).getId());
								Log.w("majs", "VIDEO URL: " + videoUrl);

								getActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										final VideoView video = (VideoView) rootView.findViewById(R.id.remoteVideo);
										video.setVisibility(VideoView.VISIBLE);
										video.setVideoPath(videoUrl);
										
										final MediaController mc = new MediaController(getActivity().getApplicationContext());
										mc.setMediaPlayer(video);
										video.requestFocus();
//										video.start();
										video.setOnInfoListener(new MediaPlayer.OnInfoListener() {
											@Override
											public boolean onInfo(MediaPlayer mp, int what, int extra) {
												if(what == 3) {
													Log.w(MainActivity.LOG_TAG, "START VIDEO");
												} else if(what == 700) {
													Log.w(MainActivity.LOG_TAG, "LAGGING VIDEO " + extra);
												}{
													Log.w(MainActivity.LOG_TAG, "Info " + what + " " + extra);
												}
												return false;
											}
										});
										video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
											@Override
											public void onPrepared(MediaPlayer mp) {
												if(localVideo != null) {
													localVideo.start();
												}
												video.start();
											}
										});
										remoteVideo = video;
									}
								});
							}
							else
							{
								remote = BitmapFactory.decodeStream(u.openConnection().getInputStream());
							}
						} else {
							if(api.invalidTokens()) {
								setStatus("Tokens invalid");
								getActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										at.execute("");
									}
								});
							} else {
								setStatus("Found none");

								if(videos) {
									getActivity().runOnUiThread(new Runnable() {
										@Override
										public void run() {
											if(localVideo != null) {
												localVideo.start();
											}
										}
									});
								}
							}

						}
					} else {
						currentFile = null;
						local = null;
						remote = null;
					}
				}
				
			} catch (Exception e) {
				Log.w("majs", "Exception in FetchNextPhoto doInBackground");
				Log.w("majs", e.getMessage());
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			 ImageView img = (ImageView) rootView.findViewById(R.id.remoteImage);
			 img.setImageBitmap(remote);
		}			
	}
	final AsyncTask<String, Integer, String> att = new FetchNextPhoto();
	
	public File getAlbumStorageDir() {
		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		
	    return path;
	}

	public void setStatus(final String text) {
		if(status != null) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					status.setText(text);
				}
			});
		}
	}
	
	private void setSize(final long biggestSize) {
		if(size != null) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(biggestSize<1024) {
						size.setText("" + biggestSize +" B");
					} else if(biggestSize<1024*1024) {
						size.setText("" + (biggestSize/1024) + " KiB");
					} else if(biggestSize<1024*1024*1024) {
						size.setText("" + (biggestSize/(1024*1024)) + " MiB");
					} else if(biggestSize<1024*1024*1024*1024) {
						size.setText("" + (biggestSize/(1024*1024*1024)) + " GiB");
					}
				}
			});
		}
	}
	
	private void clearImages(final View rootView) {
		ImageView img = (ImageView) rootView.findViewById(R.id.remoteImage);
		img.setImageBitmap(null);

		img = (ImageView) rootView.findViewById(R.id.localImage);
		img.setImageBitmap(null);
	}

	public void gotoNext() {
		ImageView img = rootView.findViewById(R.id.remoteImage);
		img.setImageBitmap(null);

		img = rootView.findViewById(R.id.localImage);
		img.setImageBitmap(null);

		new FetchNextPhoto().execute("");
		final Button deleteBtn = (Button) rootView.findViewById(R.id.deleteBtn);
		deleteBtn.setBackgroundResource(android.R.drawable.btn_default);

		Log.w(MainActivity.LOG_TAG, "Went to next");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		
		this.rootView = rootView;
		
		status = (TextView) rootView.findViewById(R.id.statusText);
		status.setText("Started");

		size = (TextView) rootView.findViewById(R.id.imageSize);
		size.setText("0B");
		
		Button clear = (Button) rootView.findViewById(R.id.clearBtn);
		clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences prefs = getActivity().getPreferences(0);
				Editor edit = prefs.edit();
				edit.clear();
				edit.commit();
			}
		});

		SharedPreferences prefs = getActivity().getPreferences(0);

		final AsyncTask<String, Integer, String> att = new FetchNextPhoto();
		
		final Button deleteBtn = (Button) rootView.findViewById(R.id.deleteBtn);
		deleteBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.w(MainActivity.LOG_TAG, "We want to delete " + currentFile);
				if(currentFile.canWrite()) {
					if(currentFile.delete()) {
						setStatus("Successfully deleted " + currentFile.getName());
					} else {
						skipped.add(currentFile);
						setStatus("Failed deleting!");
					}
					clearImages(rootView);
					new FetchNextPhoto().execute("");
					deleteBtn.setBackgroundResource(android.R.drawable.btn_default);
				} else {
					Log.w(MainActivity.LOG_TAG, "Doing stuff to delete");
					class Image {
						private final Uri uri;
						private final String name;
						private final int width;
						private final int height;
						private final int size;

						public Image(Uri uri, String name, int width, int height, int size) {
							this.uri = uri;
							this.name = name;
							this.width = width;
							this.height = height;
							this.size = size;
						}
					}
					List<Image> videoList = new ArrayList<Image>();

					Uri collection;
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
						collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
					} else {
						collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
					}
					String[] projection = new String[] {
							MediaStore.Images.Media._ID,
							MediaStore.Images.Media.DISPLAY_NAME,
							MediaStore.Images.Media.WIDTH,
							MediaStore.Images.Media.HEIGHT,
							MediaStore.Images.Media.SIZE
					};
					String selection = MediaStore.Images.Media.WIDTH +
							" >= ?";
					String[] args = new String[]{"300"};
					String sortOrder = MediaStore.Images.Media.DISPLAY_NAME + " ASC";
					try (Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(
							collection,
							projection,
							selection,
							args,
							sortOrder
					)) {
						// Cache column indices.
						int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
						int nameColumn =
								cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
						int widthColumn =
								cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH);
						int heightColumn =
								cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT);
						int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);

						while (cursor.moveToNext()) {
							// Get values of columns for a given video.
							long id = cursor.getLong(idColumn);
							String name = cursor.getString(nameColumn);
							int width = cursor.getInt(widthColumn);
							int height = cursor.getInt(heightColumn);
							int size = cursor.getInt(sizeColumn);

							Uri contentUri = ContentUris.withAppendedId(
									MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

							// Stores column values and the contentUri in a local object
							// that represents the media file.
							videoList.add(new Image(contentUri, name, width, height, size));
							//Log.w(MainActivity.LOG_TAG, "Image: " + name + " : " + size + ":" + contentUri);


							if(currentFile.getName().contains(name)) {
								Log.w(MainActivity.LOG_TAG, "Image: " + name + " : " + size + ":" + contentUri + " Uri: " + contentUri);

								toDelete.add(contentUri);
								if(toDelete.size() > 2) {
									List<Uri> uris = new ArrayList<Uri>();
									uris.add(contentUri);
									PendingIntent deleteRequest = MediaStore.createDeleteRequest(getActivity().getContentResolver(), toDelete);
									try {
//									deleteRequest.send();
										getActivity().startIntentSenderForResult(deleteRequest.getIntentSender(),
												10,
												null,
												0,
												0,
												0);
										//								getActivity().getApplicationContext().startActivity(deleteRequest);
										Log.w(MainActivity.LOG_TAG, "Deleted");
										toDelete.clear();
									} catch (Exception e) {
										Log.w(MainActivity.LOG_TAG, "Exce..." + e);
										//getIntentSender().sendIntent(getActivity().getApplicationContext(), deleteRequest);
									}
									Log.w(MainActivity.LOG_TAG, "Done");
								} else {
									Log.w(MainActivity.LOG_TAG, "Only got this many to delete: " + toDelete.size());
									skipped.add(currentFile);
									gotoNext();
									
								}

							}
						}
					}

//					getActivity().getContentResolver().delete(Uri.fromFile(currentFile), null, null);
				}
			}
		});

		Button skipBtn = (Button) rootView.findViewById(R.id.skipBtn);
		skipBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(currentFile != null) {
					if(api != null && api.invalidTokens()) {
						at.execute("");
						return;
					}
					setStatus("Skipped");
					skipped.add(currentFile);

					clearImages(rootView);
					
					Button deleteBtn = (Button) rootView.findViewById(R.id.deleteBtn);
					deleteBtn.setBackgroundResource(android.R.drawable.btn_default);
					new FetchNextPhoto().execute("");
				}
			}
		});
		
		if(!api.hasTokens(prefs) || api.invalidTokens()) {
			setStatus("Need login");
			at.execute("");
		} else {
			setStatus("Tokens exist");
			api.readTokens(prefs);
			att.execute("");
		}

		Button filter = (Button) rootView.findViewById(R.id.filterButton);
		filter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(getActivity(), Filter.class);
				//myIntent.putExtra("key", value); //Optional parameters
				startActivity(myIntent);
			}
		});


		return rootView;
	}

	public void gotTokens(String accessToken, String tokenSecret) {
		api.setTokens(accessToken, tokenSecret);
		
		setStatus("Stored tokens");
		api.storeTokens(getActivity().getPreferences(0));
	}
	
	public boolean toggleVideo() {
		videos = !videos;
		return videos;
	}
}