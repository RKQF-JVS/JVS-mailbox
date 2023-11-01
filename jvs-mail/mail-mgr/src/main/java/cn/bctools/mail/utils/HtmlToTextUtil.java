package cn.bctools.mail.utils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

/**
 * @author R
 */
public class HtmlToTextUtil {
    public static String toPlainText(String html) {
        if (html == null) {
            return "";
        }
        final Document document = Jsoup.parse(html);
        final Document.OutputSettings outputSettings = new Document.OutputSettings().prettyPrint(false);
        document.outputSettings(outputSettings);
        document.select("br").append("\\n");
        document.select("p").prepend("\\n");
        document.select("p").append("\\n");
        final String newHtml = document.html().replaceAll("\\\\n", "\n");
        final String plainText = Jsoup.clean(newHtml, "", Whitelist.none(), outputSettings);
        final String result = StringEscapeUtils.unescapeHtml4(plainText.trim());
        String s = "";
        int i = 20;
        if (result.length() > i) {
            s = result.substring(0, 20);
        } else {
            s = result;
        }
        return s;
    }
}
