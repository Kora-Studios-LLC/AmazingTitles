package org.korastudios.amazingtitles.code.api.interfaces;

import org.korastudios.amazingtitles.code.internal.components.ComponentArguments;

import java.util.LinkedList;
import java.util.List;

public interface FramesBuilder {
	
	LinkedList<String> buildFrames(ComponentArguments arguments, String[] args);
	
}
