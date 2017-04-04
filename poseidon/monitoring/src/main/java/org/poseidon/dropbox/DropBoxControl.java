package org.poseidon.dropbox;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.imageio.ImageIO;

import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.poseidon.EventDetails;
import org.poseidon.OutputControl;
import org.poseidon.camera.Camera;
import org.poseidon.camera.SecurityCameraEvent;

import com.dropbox.core.DbxAuthInfo;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxHost;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;

public class DropBoxControl implements OutputControl {
	public static int RESOURCE_ID=77;
	private static final int DROPBOX_ACCESS_TOKEN = 31;

	private String ACCESS_TOKEN=null;
	private DbxClientV2 dbxClient=null;
	private String dropboxPath="/security";

	public void initialize()
	{
	DbxAuthInfo authInfo = new DbxAuthInfo(ACCESS_TOKEN, DbxHost.DEFAULT);
	DbxRequestConfig requestConfig = new DbxRequestConfig("camera-files");
	dbxClient = new DbxClientV2(requestConfig, authInfo.getAccessToken(), authInfo.getHost());
	}
public void uploadFile(BufferedImage image)
{
	 try {
		 if(ACCESS_TOKEN!=null)
		 {
		FileMetadata metadata = dbxClient.files().uploadBuilder(dropboxPath+new Date())
		         .withMode(WriteMode.ADD)
		         .withClientModified(new Date())
		         .uploadAndFinish(toByteArrayInputStream(image));
		 }
	} catch (DbxException | IOException e) {
		e.printStackTrace();
	}
}
	private InputStream toByteArrayInputStream(BufferedImage image) throws IOException {
		final ByteArrayOutputStream output = new ByteArrayOutputStream() {
		    @Override
		    public synchronized byte[] toByteArray() {
		        return this.buf;
		    }
		};
		ImageIO.write(image, "jpg", output);
		return new ByteArrayInputStream(output.toByteArray(), 0, output.size());
}
	@Override
	public void eventReceived(int resourceId, EventDetails eventDetails) {
		switch(resourceId)
		{
		case Camera.RESOURCE_ID:uploadFile(((SecurityCameraEvent)eventDetails).getPreviousImage());
		uploadFile(((SecurityCameraEvent)eventDetails).getCurrentImage());break;
		}

	}

	@Override
	public ExecuteResponse execute(int resourceid, String params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset(int resourceid) {
		// TODO Auto-generated method stub

	}

	@Override
	public WriteResponse writeValue(int rId,LwM2mResource resource) {
		switch(rId)
		{

		case DROPBOX_ACCESS_TOKEN:ACCESS_TOKEN=(String)resource.getValue();

		}
		return WriteResponse.success();
	}

}
