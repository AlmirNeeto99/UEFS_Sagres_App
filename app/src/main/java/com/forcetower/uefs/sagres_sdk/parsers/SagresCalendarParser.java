package com.forcetower.uefs.sagres_sdk.parsers;

import android.util.Log;

import com.forcetower.uefs.database.entities.ACalendarItem;
import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.domain.SagresCalendarItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import static com.forcetower.uefs.Constants.APP_TAG;

/**
 * Created by João Paulo on 02/12/2017.
 */
public class SagresCalendarParser {

    public static List<ACalendarItem> getCalendar(Document document) {
        List<ACalendarItem> items = new ArrayList<>();

        Element element = document.selectFirst("div[class=\"webpart-calendario\"]");
        if (element == null) {
            return items;
        }

        if (element.childNodeSize() < 2) {
            Log.i(APP_TAG, "Incorrect amount of children");
            return items;
        }


        Element events = element.child(1);
        Element ul = events.selectFirst("ul");

        for (Element li : ul.select("li")) {
            String text = li.text();
            int index = text.indexOf("-");
            String days = text.substring(0, index);
            String event = text.substring(index + 1);
            items.add(new ACalendarItem(days, event.trim()));
        }

        return items;
    }
}
