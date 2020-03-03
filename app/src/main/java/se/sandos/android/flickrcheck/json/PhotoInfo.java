package se.sandos.android.flickrcheck.json;

import java.util.List;

import se.sandos.android.flickrcheck.json.PhotoSetList.TextContent;


public class PhotoInfo {
	public static class Photo {
		public String id;
		public String secret;
		public String server;
		public String farm;
		public int dateuploaded;
		public int isfavorite;
		public int license;
		public int safety_level;
		public int rotation;
		public String originalsecret;
		public String originalformat;
		public static class User {
			public String nsid;
			public String username;
			public String realname;
			public String location;
			public String iconserver;
			public String iconfarm;
			public String path_alias;
			public String getNsid() {
				return nsid;
			}
			public void setNsid(String nsid) {
				this.nsid = nsid;
			}
			public String getUsername() {
				return username;
			}
			public void setUsername(String username) {
				this.username = username;
			}
			public String getRealname() {
				return realname;
			}
			public void setRealname(String realname) {
				this.realname = realname;
			}
			public String getLocation() {
				return location;
			}
			public void setLocation(String location) {
				this.location = location;
			}
			public String getIconserver() {
				return iconserver;
			}
			public void setIconserver(String iconserver) {
				this.iconserver = iconserver;
			}
			public String getIconfarm() {
				return iconfarm;
			}
			public void setIconfarm(String iconfarm) {
				this.iconfarm = iconfarm;
			}
			public String getPath_alias() {
				return path_alias;
			}
			public void setPath_alias(String path_alias) {
				this.path_alias = path_alias;
			}
			
		}
		public User owner;
		public TextContent title;
		public TextContent description;
		public static class Visibility {
			public int ispublic;
			public int isfriend;
			public int isfamily;
			public int getIspublic() {
				return ispublic;
			}
			public void setIspublic(int ispublic) {
				this.ispublic = ispublic;
			}
			public int getIsfriend() {
				return isfriend;
			}
			public void setIsfriend(int isfriend) {
				this.isfriend = isfriend;
			}
			public int getIsfamily() {
				return isfamily;
			}
			public void setIsfamily(int isfamily) {
				this.isfamily = isfamily;
			}
			
		}
		public Visibility visibility;
		public static class Dates {
			public long posted;
			public String taken;
			public int takengranularity;
			public long lastupdate;
			
			public long getPosted() {
				return posted;
			}

			public void setPosted(long posted) {
				this.posted = posted;
			}

			public String getTaken() {
				return taken;
			}

			public void setTaken(String taken) {
				this.taken = taken;
			}

			public int getTakengranularity() {
				return takengranularity;
			}

			public void setTakengranularity(int takengranularity) {
				this.takengranularity = takengranularity;
			}

			public long getLastupdate() {
				return lastupdate;
			}

			public void setLastupdate(long lastupdate) {
				this.lastupdate = lastupdate;
			}

			@Override
			public String toString() {
				return "Dates [posted=" + posted + ", taken=" + taken
						+ ", takengranularity=" + takengranularity
						+ ", lastupdate=" + lastupdate + "]";
			}
		}
		public Dates dates;
		public static class Permissions {
			public int permcomment;
			public int permaddmeta;
			public int getPermcomment() {
				return permcomment;
			}
			public void setPermcomment(int permcomment) {
				this.permcomment = permcomment;
			}
			public int getPermaddmeta() {
				return permaddmeta;
			}
			public void setPermaddmeta(int permaddmeta) {
				this.permaddmeta = permaddmeta;
			}
			
		}
		
		public Permissions permissions;
		public int views;
		public static class Editiability {
			public int cancomment;
			public int canaddmeta;
			public int getCancomment() {
				return cancomment;
			}
			public void setCancomment(int cancomment) {
				this.cancomment = cancomment;
			}
			public int getCanaddmeta() {
				return canaddmeta;
			}
			public void setCanaddmeta(int canaddmeta) {
				this.canaddmeta = canaddmeta;
			}
			
		}
		public Editiability editability;
		public Editiability publiceditability;
		public static class Usage {
			public int candownload;
			public int canblog;
			public int canprint;
			public int canshare;
			
			public int getCanprint() {
				return canprint;
			}
			public void setCanprint(int canprint) {
				this.canprint = canprint;
			}
			public int getCanshare() {
				return canshare;
			}
			public void setCanshare(int canshare) {
				this.canshare = canshare;
			}
			public int getCandownload() {
				return candownload;
			}
			public void setCandownload(int candownload) {
				this.candownload = candownload;
			}
			public int getCanblog() {
				return canblog;
			}
			public void setCanblog(int canblog) {
				this.canblog = canblog;
			}
			
		}
		
		public Usage usage;
		public TextContent comments;
		//Notes skipped
		public static class Tag {
			public String id;
			public String author;
			public String authorname;
			public String raw;
			public String _content;
			public int machine_tag;
			public String getId() {
				return id;
			}
			public void setId(String id) {
				this.id = id;
			}
			public String getAuthor() {
				return author;
			}
			public void setAuthor(String author) {
				this.author = author;
			}
			public String getAuthorname() {
				return authorname;
			}
			public void setAuthorname(String authorname) {
				this.authorname = authorname;
			}
			public String getRaw() {
				return raw;
			}
			public void setRaw(String raw) {
				this.raw = raw;
			}
			public String get_content() {
				return _content;
			}
			public void set_content(String _content) {
				this._content = _content;
			}
			public int getMachine_tag() {
				return machine_tag;
			}
			public void setMachine_tag(int machine_tag) {
				this.machine_tag = machine_tag;
			}
			@Override
			public String toString() {
				return "Tag [id=" + id + ", author=" + author + ", authorname="
						+ authorname + ", raw=" + raw + ", _content="
						+ _content + ", machine_tag=" + machine_tag + "]";
			}
			
			
		}
		
		public static class TagContainer {
			public List<Tag> tag;

			public List<Tag> getTag() {
				return tag;
			}

			public void setTag(List<Tag> tag) {
				this.tag = tag;
			}

			@Override
			public String toString() {
				return "TagContainer [tag=" + tag + "]";
			}
		}
		
		public TagContainer tags;
		
		public static class Location {
			public float latitude;
			public float longitude;
			public int accuracy;
			public int context;
			//Lots skipped
			public float getLatitude() {
				return latitude;
			}
			public void setLatitude(float latitude) {
				this.latitude = latitude;
			}
			public float getLongitude() {
				return longitude;
			}
			public void setLongitude(float longitude) {
				this.longitude = longitude;
			}
			public int getAccuracy() {
				return accuracy;
			}
			public void setAccuracy(int accuracy) {
				this.accuracy = accuracy;
			}
			public int getContext() {
				return context;
			}
			public void setContext(int context) {
				this.context = context;
			}
			
		}
		public Location location;
		
		//geoperms skipped
		//urls skipped
		public String media;

		public Dates getDates() {
			return dates;
		}

		public void setDates(Dates dates) {
			this.dates = dates;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
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

		public int getDateuploaded() {
			return dateuploaded;
		}

		public void setDateuploaded(int dateuploaded) {
			this.dateuploaded = dateuploaded;
		}

		public int getIsfavorite() {
			return isfavorite;
		}

		public void setIsfavorite(int isfavorite) {
			this.isfavorite = isfavorite;
		}

		public int getLicense() {
			return license;
		}

		public void setLicense(int license) {
			this.license = license;
		}

		public int getSafety_level() {
			return safety_level;
		}

		public void setSafety_level(int safety_level) {
			this.safety_level = safety_level;
		}

		public int getRotation() {
			return rotation;
		}

		public void setRotation(int rotation) {
			this.rotation = rotation;
		}

		public String getOriginalsecret() {
			return originalsecret;
		}

		public void setOriginalsecret(String originalsecret) {
			this.originalsecret = originalsecret;
		}

		public String getOriginalformat() {
			return originalformat;
		}

		public void setOriginalformat(String originalformat) {
			this.originalformat = originalformat;
		}

		public User getOwner() {
			return owner;
		}

		public void setOwner(User owner) {
			this.owner = owner;
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

		public Visibility getVisibility() {
			return visibility;
		}

		public void setVisibility(Visibility visibility) {
			this.visibility = visibility;
		}

		public Dates getTaken() {
			return dates;
		}

		public void setTaken(Dates taken) {
			this.dates = taken;
		}

		public Permissions getPermissions() {
			return permissions;
		}

		public void setPermissions(Permissions permissions) {
			this.permissions = permissions;
		}

		public int getViews() {
			return views;
		}

		public void setViews(int views) {
			this.views = views;
		}

		public Editiability getEditability() {
			return editability;
		}

		public void setEditability(Editiability editability) {
			this.editability = editability;
		}

		public Editiability getPubliceditability() {
			return publiceditability;
		}

		public void setPubliceditability(Editiability publiceditability) {
			this.publiceditability = publiceditability;
		}

		public Usage getUsage() {
			return usage;
		}

		public void setUsage(Usage usage) {
			this.usage = usage;
		}

		public TextContent getComments() {
			return comments;
		}

		public void setComments(TextContent comments) {
			this.comments = comments;
		}

		public TagContainer getTags() {
			return tags;
		}

		public void setTags(TagContainer tags) {
			this.tags = tags;
		}

		public Location getLocation() {
			return location;
		}

		public void setLocation(Location location) {
			this.location = location;
		}

		public String getMedia() {
			return media;
		}

		public void setMedia(String media) {
			this.media = media;
		}

		@Override
		public String toString() {
			return "Photo [id=" + id + ", secret=" + secret + ", server="
					+ server + ", farm=" + farm + ", dateuploaded="
					+ dateuploaded + ", isfavorite=" + isfavorite
					+ ", license=" + license + ", safety_level=" + safety_level
					+ ", rotation=" + rotation + ", originalsecret="
					+ originalsecret + ", originalformat=" + originalformat
					+ ", owner=" + owner + ", title=" + title
					+ ", description=" + description + ", visibility="
					+ visibility + ", dates=" + dates + ", permissions="
					+ permissions + ", views=" + views + ", editability="
					+ editability + ", publiceditability=" + publiceditability
					+ ", usage=" + usage + ", comments=" + comments + ", tags="
					+ tags + ", location=" + location + ", media=" + media
					+ "]";
		}

		
		
	}
	
	public Photo photo;

	public Photo getPhoto() {
		return photo;
	}

	public void setPhoto(Photo photo) {
		this.photo = photo;
	}

	@Override
	public String toString() {
		return photo.toString();
	}
}
