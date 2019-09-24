package com.ichat.command;

import com.ichat.common.Constants;
import com.ichat.service.ByteArrayFile;
import org.apache.commons.collections4.MapUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Downloads extends AbstractStringCommand {

    private static final char LIST_PARAM = 'l';

    @Override
    public String getDescription() {
        return "Allows for the management of downloads";
    }

    @Override
    public String process(Map<String, String> paramArgMap) {
        if (MapUtils.isNotEmpty(paramArgMap) && paramArgMap.containsKey(HELP_PARAM)) {
            return help();
        }
        return generateFileListHTML(getDownloadedFiles());
    }

    @Override
    public Character getDefaultParameter() {
        return LIST_PARAM;
    }

    @Override
    protected void initializeParams() {
        super.initializeParams();
        this.addParameter(LIST_PARAM, buildHelpText(LIST_PARAM, "list","List all prior downloads. Default."));
    }

    private List<ByteArrayFile> getDownloadedFiles() {
        //get all the files in the directory
        File downloadsDir = new File(Constants.SERVER_FILE_STORAGE_PATH);
        if (!(downloadsDir.exists() && downloadsDir.isDirectory())) {
            throw new IllegalStateException("Unable to open the downloads directory!");
        }
        List<ByteArrayFile> files = new ArrayList<>();
        File[] downloads = downloadsDir.listFiles();
        Stream.of(downloads).forEach(d -> {
            if (!d.isDirectory()) {
                try {
                    String fileId = d.getName().substring(0, 7);
                    String realFileName = d.getName().substring(8);
                    ByteArrayFile file = new ByteArrayFile(realFileName, null);
                    file.setId(fileId);
                    files.add(file);
                } catch (StringIndexOutOfBoundsException s) {
                    System.out.println("Unable add " + d.getName() + " to downloads list!");
                }
            }
        });
        //extract the real file name
        return files;
    }

    public String generateFileListHTML(List<ByteArrayFile> files) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<div class=\"system-message\">Downloads:<br>");
        htmlBuilder.append("<ul>");
        for (ByteArrayFile f : files) {
            htmlBuilder.append(String.format("<li><a href=\"%s\" class=\"file-download\">%s</a></li>", f.getUniqueFileName(), f.getName()));
        }
        htmlBuilder.append("</ul>");
        htmlBuilder.append("</div>");
        return htmlBuilder.toString();
    }
}
