/*
 * Java Payloads.
 * 
 * Copyright (c) 2010, 2011 Michael 'mihi' Schierl
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *   
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *   
 * - Neither name of the copyright holders nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *   
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND THE CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDERS OR THE CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package javapayload.handler.stage;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.util.zip.GZIPOutputStream;

public class GZIP extends FilterStageHandler {
		
	public GZIP() {
		this(false);
	}
	
	protected GZIP(boolean extraSmall) {
		super("Compress stage and initial upload with GZIP",
				"Compress the stage and initial upload with GZIP. The following communication\r\n" +
				"will remain uncompressed."+(extraSmall ? " This is a more compact stage that might not work\r\n" +
						"with all stagers. If it does not work, use the GZIP stage." : ""));
	}
	
	protected void customUpload(DataOutputStream out, String[] parameters) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(baos));
		StageHandler realStageHandler = findRealStageHandler(parameters);
		realStageHandler.handleBootstrap(realParameters, dos);
		dos.close();
		out.writeInt(baos.size());
		out.write(baos.toByteArray());
	}

	protected void handleStreams(DataOutputStream out, InputStream in, String[] parameters) throws Exception {
		findRealStageHandler(parameters).handleStreams(out, in, realParameters);
	}
	
	public Class[] getNeededClasses() {
		return new Class[] { javapayload.stage.Stage.class, javapayload.stage.GZIP.class };
	}
	
	protected StageHandler createClone() {
		return new GZIP();
	}
}