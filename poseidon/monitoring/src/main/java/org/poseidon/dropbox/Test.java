package org.poseidon.dropbox;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {

	public static void main(String[] args) {
		System.out.println(new SimpleDateFormat("yyyyMMddHHmmssSSSz.'"+"jpg"+"'").format(new Date()));

	}

}
