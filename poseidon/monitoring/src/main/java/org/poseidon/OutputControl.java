package org.poseidon;

import org.eclipse.leshan.core.response.WriteResponse;

public interface OutputControl extends GenericControl{

	WriteResponse writeValue(int rId);

	

}
