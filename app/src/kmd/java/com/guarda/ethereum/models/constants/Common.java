package com.guarda.ethereum.models.constants;

import java.math.BigDecimal;

public class Common {

    public final static String BLOCK = "passphrase";

    public final static String BTC_NODE_LOGIN = "";
    public final static String BTC_NODE_PASS = "";

    public final static String NODE_ADDRESS = "";
    public final static String MAIN_CURRENCY = "dic";
    public final static String MAIN_CURRENCY_NAME = "dic";
    public final static String ETH_SHOW_PATTERN = "#,##0.#####";

    /*http://172.104.191.125:48906*/
    //public final static String KMD_EXPLORER_BASE_URL = "https://120.78.1.111:8080";
    //public final static String KMD_EXPLORER_BASE_URL = "http://172.104.191.125:45893";
    //public final static String KMD_EXPLORER_BASE_URL_01 = "http://172.104.191.125:48906";
    public final static String KMD_EXPLORER_BASE_URL = "https://explorer.indexchain.io";
    public final static String KMD_EXPLORER_BASE_URL_01 = "http://explorer1.indexchain.io:56692";
    //public final static String KMD_EXPLORER_TRANSACTION_HASH_URL = "https://explorer.indexchain.io/tx/";
    //reward节点钱包地址
    public final static String REWARD_NODE_ADDR = "";

    public final static String TERM_OF_USE_LINK = "https://guarda.co/terms-of-service?isWebView=1";
    public final static String PRIVACY_POLICE_LINK = "https://guarda.co/privacy-policy?isWebView=1";
    public final static String ABOUT_APP_LINK = "https://guarda.co/about";
    public final static String SITE_APP_LINK = "https://guarda.co/";

    /*extras*/
    public final static String EXTRA_TRANSACTION_POSITION = "EXTRA_TRANSACTION_POSITION";

    public final static int DEFAULT_GAS_LIMIT = 21000;
    public final static int DEFAULT_GAS_LIMIT_FOR_CONTRACT = 21000;
    public final static int KMD_MIN_CONFIRM = 1;

    public final static String TOKENS_FILE_NAME = "etc_tokens_request.json";
    public final static String EXTRA_FIELDS = "extra-fields.json";

    public final static String BIP_39_WORDLIST_ASSET = "bip39-wordlist.txt";
    public final static int MNEMONIC_WORDS_COUNT = 12;

    public final static BigDecimal AVG_TX_SIZE_KB = new BigDecimal("0.650");
}
