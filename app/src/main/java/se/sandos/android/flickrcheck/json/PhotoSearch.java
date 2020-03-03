package se.sandos.android.flickrcheck.json;

import java.util.List;

public class PhotoSearch {
	public static class Photos {
		public int page;
		public int pages;
		public int perpage;
		public int total;
		
		public static class Photo {
			public String id;
			public String owner;
			public String secret;
			public String server;
			public String farm;
			public String title;
			public int isfriend;
			public int ispublic;
			public int isfamily;

			public String baseUrl(String suffix) {
				return "https://farm" + farm + ".staticflickr.com/" + server + "/" + id + "_" + secret + suffix + ".jpg";
			}
			
			public String getId() {
				return id;
			}
			public void setId(String id) {
				this.id = id;
			}
			public String getOwner() {
				return owner;
			}
			public void setOwner(String owner) {
				this.owner = owner;
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
			public String getTitle() {
				return title;
			}
			public void setTitle(String title) {
				this.title = title;
			}
			public int getIsfriend() {
				return isfriend;
			}
			public void setIsfriend(int isfriend) {
				this.isfriend = isfriend;
			}
			public int getIspublic() {
				return ispublic;
			}
			public void setIspublic(int ispublic) {
				this.ispublic = ispublic;
			}
			public int getIsfamily() {
				return isfamily;
			}
			public void setIsfamily(int isfamily) {
				this.isfamily = isfamily;
			}

		}
		
		public List<Photo> photo;

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

		public List<Photo> getPhoto() {
			return photo;
		}

		public void setPhoto(List<Photo> photo) {
			this.photo = photo;
		}
	}
	
	public Photos photos;

	public Photos getPhotos() {
		return photos;
	}

	public void setPhotos(Photos photos) {
		this.photos = photos;
	}
	
	
}
