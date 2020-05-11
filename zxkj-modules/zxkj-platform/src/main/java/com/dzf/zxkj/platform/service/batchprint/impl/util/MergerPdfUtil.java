package com.dzf.zxkj.platform.service.batchprint.impl.util;

import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.File;
import java.io.IOException;

public class MergerPdfUtil {


    private static String[] getFiles(String folder) throws IOException {
        File _folder = new File(folder);
        String[] filesInFolder;

        if (_folder.isDirectory()) {
            filesInFolder = _folder.list();
            return filesInFolder;
        } else {
            throw new IOException("路径不对");
        }
    }


    public static void main(String[] args) {
        PDFMergerUtility mergePdf = new PDFMergerUtility();
        String folder = "C:/Users/admin/Desktop/pdf";
        System.out.println(folder);
        String destinationFileName = "C:/Users/admin/Desktop/pdf/合成.pdf";
        try {
            String[] filesInFolder = getFiles(folder);
            for (int i = 0; i < filesInFolder.length; i++)
                mergePdf.addSource(folder + File.separator + filesInFolder[i]);
            mergePdf.setDestinationFileName(destinationFileName);
            mergePdf.mergeDocuments();
            System.out.print("合并完成");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
