package se.sandos.android.flickrcheck.json;

import java.util.Date;
import java.util.List;

public class PhotoSetList {
	public static class TextContent {
		public String _content;

		public String get_content() {
			return _content;
		}

		public void set_content(String _content) {
			this._content = _content;
		}
	}
	
	public static class PhotoSets {
		public static class PhotoSet {
			public String id;
			public String primary;
			public String secret;
			public String server;
			public String farm;
			public int photos;
			public int videos;
			public int count_views;
			public int count_comments;
			public int can_comment;
			public Long date_create;
			public Long date_update;
			public TextContent title;
			public TextContent description;
			
			public String getId() {
				return id;
			}
			public void setId(String id) {
				this.id = id;
			}
			public String getPrimary() {
				return primary;
			}
			public void setPrimary(String primary) {
				this.primary = primary;
			}
			public String getSecret() {
				return secret;
			}
			public void setSecret(String secret) {
				this.secret = secret;
			}
			public String getServer() {
				return server;
			}
			public void setServer(String server) {
				this.server = server;
			}
			public String getFarm() {
				return farm;
			}
			public void setFarm(String farm) {
				this.farm = farm;
			}
			public int getPhotos() {
				return photos;
			}
			public void setPhotos(int photos) {
				this.photos = photos;
			}
			public int getVideos() {
				return videos;
			}
			public void setVideos(int videos) {
				this.videos = videos;
			}
			public int getCount_views() {
				return count_views;
			}
			public void setCount_views(int count_views) {
				this.count_views = count_views;
			}
			public int getCount_comments() {
				return count_comments;
			}
			public void setCount_comments(int count_comments) {
				this.count_comments = count_comments;
			}
			public int getCan_comment() {
				return can_comment;
			}
			public void setCan_comment(int can_comment) {
				this.can_comment = can_comment;
			}

			public Long getDate_create() {
				return date_create;
			}
			public void setDate_create(Long date_create) {
				this.date_create = date_create;
			}
			public Long getDate_update() {
				return date_update;
			}
			public void setDate_update(Long date_update) {
				this.date_update = date_update;
			}
			public TextContent getTitle() {
				return title;
			}
			public void setTitle(TextContent title) {
				this.title = title;
			}
			public TextContent getDescription() {
				return description;
			}
			public void setDescription(TextContent description) {
				this.description = description;
			}
		}
		
		public String cancreate;
		public int page;
		public int pages;
		public int perpage;
		public int total;
		public List<PhotoSet> photoset;
		
		public String getCancreate() {
			return cancreate;
		}
		public void setCancreate(String cancreate) {
			this.cancreate = cancreate;
		}
		public int getPage() {
			return page;
		}
		public void setPage(int page) {
			this.page = page;
		}
		public int getPages() {
			return pages;
		}
		public void setPages(int pages) {
			this.pages = pages;
		}
		public int getPerpage() {
			return perpage;
		}
		public void setPerpage(int perpage) {
			this.perpage = perpage;
		}
		public int getTotal() {
			return total;
		}
		public void setTotal(int total) {
			this.total = total;
		}
		public List<PhotoSet> getPhotoset() {
			return photoset;
		}
		public void setPhotoset(List<PhotoSet> photoset) {
			this.photoset = photoset;
		}
	
	}
	
	public PhotoSets photosets;

	public PhotoSets getPhotosets() {
		return photosets;
	}

	public void setPhotosets(PhotoSets photosets) {
		this.photosets = photosets;
	}
}
