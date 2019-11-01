package ch02;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//adapter
public class FileProperties implements FileIO{

	Properties prop = new Properties();

	@Override
	public void readFromFile(String filename) throws IOException {
		InputStream inStream = new FileInputStream(filename);
		prop.load(inStream);
	}

	@Override
	public void writeToFile(String filename) throws IOException {
		prop.store(new FileOutputStream(filename), "comments");
	}

	@Override
	public void setValue(String key, String value) {
		prop.setProperty(key, value);
	}

	@Override
	public String getValue(String key) {
		return prop.getProperty(key);
	}

}
