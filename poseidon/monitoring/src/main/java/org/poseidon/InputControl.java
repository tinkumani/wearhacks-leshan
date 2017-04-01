package org.poseidon;

import org.eclipse.leshan.core.response.ReadResponse;

public interface InputControl extends GenericControl {

	ReadResponse readValue(int resourceId);

}
