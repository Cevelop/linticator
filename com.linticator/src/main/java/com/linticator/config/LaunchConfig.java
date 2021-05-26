package com.linticator.config;

public class LaunchConfig {

	private boolean append;

	private String captureFile;

	private boolean captureOutput;

	public LaunchConfig() {
		super();
		this.captureOutput = false;
		this.captureFile = null;
		this.append = false;
	}

	public LaunchConfig(boolean captureOutput, String captureFile, boolean append) {
		super();
		this.captureOutput = captureOutput;
		this.captureFile = captureFile;
		this.append = append;
	}

	public String getCaptureFile() {
		return captureFile;
	}

	public boolean isAppend() {
		return append;
	}

	public boolean isCaptureOutput() {
		return captureOutput;
	}

	public void setAppend(boolean append) {
		this.append = append;
	}
	
	public void setCaptureFile(String captureFile) {
		this.captureFile = captureFile;
	}
	
	public void setCaptureOutput(boolean captureOutput) {
		this.captureOutput = captureOutput;
	}
}
