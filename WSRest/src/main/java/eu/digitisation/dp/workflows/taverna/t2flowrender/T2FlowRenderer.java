package eu.digitisation.dp.workflows.taverna.t2flowrender;

import eu.digitisation.dp.utils.ExecuteShellCommand;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static java.io.File.createTempFile;
import static java.nio.file.Files.copy;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

public class T2FlowRenderer {
    private static String TAVERNA_WORKFLOW_SCRIPT_PREVIEW = "/t2flow_render.rb";
    public static final String SVG = "image/svg+xml";

    public T2FlowRenderer() throws UnsupportedEncodingException {
        File renderScript = new File(T2FlowRenderer.class.getResource("/t2flow_render.rb").getPath());

        TAVERNA_WORKFLOW_SCRIPT_PREVIEW = URLDecoder.decode(renderScript.getAbsolutePath().toString(), "UTF-8");
    }

	public String renderT2flowAsSVG(String t2flow) throws IOException {
        String t2FlowPreview = "<img ";
		File src = null, dst = null;
		InputStream t2FlowStream = new ByteArrayInputStream(t2flow.getBytes(StandardCharsets.UTF_8));
        ExecuteShellCommand executeShellCommand = new ExecuteShellCommand();

        src = createTempFile("src", ".t2flow");
        dst = createTempFile("dst", ".svg");

        String command = "ruby " + TAVERNA_WORKFLOW_SCRIPT_PREVIEW + " " + src.toString() + " " + dst.toString();

		System.out.println("Execute command: " + command);

        copy(t2FlowStream, src.toPath(), REPLACE_EXISTING);
        executeShellCommand.executeCommand(command);
        FileInputStream inputStream = new FileInputStream(dst.toString());
        try {
            t2FlowPreview += "src=\"data: image/svg+xml;";
			t2FlowPreview += IOUtils.toString(inputStream);
            t2FlowPreview += "\"/>";
        } finally {
            inputStream.close();
        }

        return t2FlowPreview;
	}

    public String renderToHtml(String t2flow, Integer width,
                               Integer height) throws IOException {
        String t2FlowPreview = "";
        StringBuilder sb = new StringBuilder("<img ");
        File src = null, dst = null;
        InputStream t2FlowStream = new ByteArrayInputStream(t2flow.getBytes(StandardCharsets.UTF_8));
        ExecuteShellCommand executeShellCommand = new ExecuteShellCommand();

        src = createTempFile("src", ".t2flow");
        dst = createTempFile("dst", ".svg");

        String command = "ruby " + TAVERNA_WORKFLOW_SCRIPT_PREVIEW + " " + src.toString() + " " + dst.toString();

        System.out.println("Execute command: " + command);

        copy(t2FlowStream, src.toPath(), REPLACE_EXISTING);
        executeShellCommand.executeCommand(command);

        if (width != null)
            sb.append("width=").append(width).append("px ");
        if (height != null)
            sb.append("height=").append(height).append("px ");
        sb.append("src=\"data:" + SVG + ";base64,");
        sb.append(printBase64Binary(readAllBytes(dst.toPath())));
        sb.append("\" />");

        t2FlowPreview += sb.toString();

        return t2FlowPreview;
    }
}
