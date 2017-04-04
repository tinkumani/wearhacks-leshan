package org.poseidon;

import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.WriteResponse;

public interface OutputControl extends GenericControl{

	WriteResponse writeValue(int rId, LwM2mResource value);

	

}
