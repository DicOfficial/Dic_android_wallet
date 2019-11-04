package com.guarda.ethereum.models.constants;

public interface KmdExplorer {
    //String KMD_EXPLORER_BASE_URL = "https://kmd.explorer.supernet.org/";
//    String KMD_EXPLORER_BASE_URL = "http://www.kmdexplorer.ru/";
    String KMD_EXPLORER_BASE_URL = Common.KMD_EXPLORER_BASE_URL;
    String KMD_TEST_EXPLORER_BASE_URL = Common.KMD_EXPLORER_BASE_URL;
    String KMD_EXPLORER_API = KMD_EXPLORER_BASE_URL + "/insight-api-komodo/";
    String KMD_TEST_EXPLORER_API = KMD_TEST_EXPLORER_BASE_URL + "/insight-api-komodo/";

    String KMD_EXPLORER_BASE_URL_01 = Common.KMD_EXPLORER_BASE_URL_01;
    String KMD_EXPLORER_API_01 = KMD_EXPLORER_BASE_URL_01 + "/insight-api-komodo/";
}
