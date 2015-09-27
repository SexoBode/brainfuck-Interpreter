package gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

//cristal anal
public class JTextComponentCombinedInputStream extends InputStream {
//	private List<InputStream> streams = new LinkedList<InputStream>();
	private InputStream lastStream = null;
	private int done = 0;
	
	@Override
	public int read() throws IOException {
		synchronized(this) {
			while(lastStream == null) {
				try {
					this.wait();
				} catch (InterruptedException e) {}
			}
		}
		
		assert(lastStream != null);
		
		int returnCode = lastStream.read();
		++done;
		if(done == 3) {
			lastStream = null;
			done = 0;
		}
		return returnCode;
	}
	
	@Override
	public int read(byte b[], int off, int len) throws IOException {
//		assert(lastStream != null);
		
		synchronized(this) {
			while(lastStream == null) {
				try {
					this.wait();
				} catch (InterruptedException e) {}
			}
		}
		
		int returnCode = lastStream.read(b, 0, 3);
		lastStream = null;
		return returnCode;
	}
	
//	public void addStream(InputStream stream) {
//		streams.add(stream);
//	}
	
	public synchronized void setSource(InputStream stream) {
		lastStream = stream;
	}
}
