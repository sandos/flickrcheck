package se.sandos.android.flickrcheck.json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sizes {
	public static class Size {
		public int canblog;
		public int canprint;
		public int candownload;
		public String stat;
		
		public static class SizeLine {
			public String label;
			public int width;
			public int height;
			public String source;
			public String url;
			public String media;
			public String getLabel() {
				return label;
			}
			public void setLabel(String label) {
				this.label = label;
			}
			public int getWidth() {
				return width;
			}
			public void setWidth(int width) {
				this.width = width;
			}
			public int getHeight() {
				return height;
			}
			public void setHeight(int height) {
				this.height = height;
			}
			public String getSource() {
				return source;
			}
			public void setSource(String source) {
				this.source = source;
			}
			public String getUrl() {
				return url;
			}
			public void setUrl(String url) {
				this.url = url;
			}
			public String getMedia() {
				return media;
			}
			public void setMedia(String media) {
				this.media = media;
			}
			
			
		}

		public List<SizeLine> size;

		public Map<String, SizeLine> getSizeLines() {
			Map<String, SizeLine> m = new HashMap<String, SizeLine>();
			for(SizeLine sl : size) {
				m.put(sl.label, sl);
			}
			return m;
		}
		
		public int getCanblog() {
			return canblog;
		}

		public void setCanblog(int canblog) {
			this.canblog = canblog;
		}

		public int getCanprint() {
			return canprint;
		}

		public void setCanprint(int canprint) {
			this.canprint = canprint;
		}

		public int getCandownload() {
			return candownload;
		}

		public void setCandownload(int candownload) {
			this.candownload = candownload;
		}

		public String getStat() {
			return stat;
		}

		public void setStat(String stat) {
			this.stat = stat;
		}

		public List<SizeLine> getSize() {
			return size;
		}

		public void setSize(List<SizeLine> size) {
			this.size = size;
		}
		
		
	}
	
	public Size sizes;

	public Size getSizes() {
		return sizes;
	}

	public void setSizes(Size sizes) {
		this.sizes = sizes;
	}
	
	
}
