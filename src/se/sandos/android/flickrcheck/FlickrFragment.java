package se.sandos.android.flickrcheck;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Set;

import se.sandos.android.flickrcheck.json.PhotoInfo;
import se.sandos.android.flickrcheck.json.PhotoInfo.Photo.Tag;
import se.sandos.android.flickrcheck.json.PhotoSearch.Photos;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.checker.R;

public class FlickrFragment extends Fragment {
	
	private FlickrApi api;
	
	private TextView status;
	private TextView size;
	private EditText codeField;
	
	private File currentFile;
	
	private Set<File> skipped;
	
	private View rootView;
	
	private boolean videos = false;
	
	private VideoView localVideo;
	private VideoView remoteVideo;

	private String location;
	
	public FlickrFragment() {
		api = new FlickrApi();
		skipped = new HashSet<File>();
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public void setVideo(boolean video) {
		videos = video;
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
					int biggest = -1;
					for (File f : getAlbumStorageDir().listFiles()) {
						Log.w(MainActivity.LOG_TAG, "Number of files in " + f + " is " + f.listFiles().length);
						if(f.listFiles().length > biggest) {
							biggest = f.listFiles().length;
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
					for(File f : biggestFolder.listFiles()) {
						if(!skipped.contains(f) && f.getName().endsWith(ending) && biggestSize < f.length()) {
							biggestFile = f;
							biggestSize = f.length();
						}
					}

					if(biggestFile != null) {
						final File f = biggestFile;

						setStatus("File " + biggestFile.getName());
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
									video.setVideoPath(f.getAbsolutePath());
									
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
						Photos search = api.search(n.substring(0, n.length()-4));
						digestMatch = false;
						if(search.getTotal() > 0) {
							PhotoInfo info = api.getPhotoInfo(search.photo.get(0).id);
							
							String sha1remote = null;
							for(Tag g : info.photo.tags.tag) {
								if(g.raw.startsWith("file:md5sum=")) {
									sha1remote = g.raw.substring(12, g.raw.length());
								}
							}
							final String remotesha = sha1remote;
							Log.w("majs", "Digest stuff " + sha1remote + "|" + digest);
							
							digest = null;
							Thread digestThread = new Thread(new Runnable() {
								@Override
								public void run() {
									FileInputStream fis = null;
									try {
										//Compute SHA1
										Log.w("majs", "start hashing");
										MessageDigest md = MessageDigest.getInstance("MD5");
										fis = new FileInputStream(f);
										byte[] buffer = new byte[1024*128];
										int read;
										while((read = fis.read(buffer)) != -1) {
											md.update(buffer, 0, read);
										}
										byte[] sha1 = md.digest();
										Log.w("majs", "done hashing");
										FetchNextPhoto.this.digest = convertToHex(sha1);
										digestMatch = remotesha.equals(digest);
										if(digestMatch) {
											getActivity().runOnUiThread(new Runnable() {
												@Override
												public void run() {
													Button deleteBtn = (Button) rootView.findViewById(R.id.deleteBtn);
													if(digestMatch) {
														deleteBtn.setBackgroundColor(0xff00dd00);
													} else {
														deleteBtn.setBackgroundResource(android.R.drawable.btn_default);
													}
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
							
							setStatus("Search found " + search.getTotal());
							URL u = new URL(search.photo.get(0).baseUrl("_n"));
							remote = BitmapFactory.decodeStream(u.openConnection().getInputStream());
							
							if(videos) {
								final String videoUrl = api.getVideoUrl(search.photo.get(0).getId());
								
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
						} else {
							setStatus("Found none");
						}
					} else {
						currentFile = null;
						local = null;
						remote = null;
					}
				}
				
			} catch (Exception e) {
				Log.w("majs", e);
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

	private void setStatus(final String text) {
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
						size.setText("" + (biggestSize/1024) + "KiB");
					} else if(biggestSize<1024*1024*1024) {
						size.setText("" + (biggestSize/(1024*1024)) + "MiB");
					} else if(biggestSize<1024*1024*1024*1024) {
						size.setText("" + (biggestSize/(1024*1024*1024)) + "GiB");
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
		
		codeField = (EditText) rootView.findViewById(R.id.code);
		
		Button button = (Button) rootView.findViewById(R.id.tokenBtn);

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
		
		AsyncTask<String, Integer, String> at = new AsyncTask<String, Integer, String>(){
			@Override
			protected String doInBackground(String... params) {

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
		
		SharedPreferences prefs = getActivity().getPreferences(0);

		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AsyncTask<String, Integer, String> att = new AsyncTask<String, Integer, String>(){
					@Override
					protected String doInBackground(String... params) {
						try {
							setStatus("Getting token");
							api.retrieveAccessToken(params[0]);
							
							SharedPreferences prefs = getActivity().getPreferences(0);
							setStatus("Got token");

							api.storeTokens(prefs);
							setStatus("Tokens stored");
						} catch (Exception e) {
							setStatus("Problem logging in: " + e.getMessage());
							Log.w("majs", e);
						}
						
						return "";
					}
				};
				
				TextView tv = (TextView) rootView.findViewById(R.id.code);
				att.execute(tv.getText().toString());
			}
		});
		
		final AsyncTask<String, Integer, String> att = new FetchNextPhoto();
		
		Button deleteBtn = (Button) rootView.findViewById(R.id.deleteBtn);
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
				}
			}
		});

		Button skipBtn = (Button) rootView.findViewById(R.id.skipBtn);
		skipBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(currentFile != null) {
					setStatus("Skipped");
					skipped.add(currentFile);

					clearImages(rootView);
					
					Button deleteBtn = (Button) rootView.findViewById(R.id.deleteBtn);
					deleteBtn.setBackgroundResource(android.R.drawable.btn_default);
					
					new FetchNextPhoto().execute("");
				}
			}
		});
		
		
		if(!api.hasTokens(prefs)) {
			setStatus("Need login");
			at.execute("");
		} else {
			setStatus("Tokens exist");
			button.setVisibility(Button.GONE);
			codeField.setVisibility(EditText.GONE);
			api.readTokens(prefs);
			att.execute("");
		}


		return rootView;
	}
}