package com.github.hronom.dockerjsonlogconverter.components.docker.json.log;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DockerJsonLogRow {

	@JsonProperty("log")
	public String log;

	@JsonProperty("stream")
	public String stream;

	@JsonProperty("time")
	public String time;
}