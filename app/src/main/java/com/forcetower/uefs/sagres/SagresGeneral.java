package com.forcetower.uefs.sagres;

import android.util.Log;

import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import static com.forcetower.uefs.Constants.APP_TAG;

/**
 * Created by João Paulo on 08/01/2018.
 */

public class SagresGeneral {

    public static boolean isLoginSuccessful(Document document) {
        if (document == null)
            return false;

        Element element = document.selectFirst("div[class=\"externo-erro\"]");
        if (element != null) {
            if (element.text().length() != 0) {
                Log.d(APP_TAG, "[Probability] Login failed by user mistake");
                return false;
            } else {
                Log.d(APP_TAG, "[Probability] Login failed by my mistake");
                return false;
            }
        } else {
            Log.d(APP_TAG, "[Probability] Correct Login");
            return true;
        }
    }

    public static String getName(Document document) {
        if (document == null)
            return "Not connected";

        Element element = document.selectFirst("span[class=\"usuario-nome\"]");
        if (element != null) {
            return Utils.toTitleCase(element.text());
        } else {
            return "Desconhecido";
        }
    }

    public static String getScore(Document document) {
        if (document == null)
            return "-";
        Element element = document.selectFirst("div[class=\"situacao-escore\"]");
        if (element != null) {
            Element score = element.selectFirst("span[class=\"destaque\"]");
            if (score != null) {
                return score.text();
            }
        }

        return "-";
    }
}
