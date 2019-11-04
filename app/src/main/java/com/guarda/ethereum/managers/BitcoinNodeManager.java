package com.guarda.ethereum.managers;


import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.guarda.ethereum.models.constants.Changelly;
import com.guarda.ethereum.rest.ApiMethods;
import com.guarda.ethereum.rest.Requestor;
import com.guarda.ethereum.utils.Coders;

import java.util.HashMap;

public class BitcoinNodeManager {

    private static String TAG = "BitcoinNodeManager";


    BitcoinNodeManager() throws Exception {

    }


    public static void sendTransaction(String rawTx, ApiMethods.RequestListener listener) {
//        TODO: check json body
        JsonArray array = new JsonArray();
        array.add(rawTx);

        JsonObject json = getMainJsonParam("sendrawtransaction");
        json.add("params", array);
        Log.d("svcom", "request json: " + json.toString());
        Requestor.sendRawTransaction(json, listener);
    }

    private static JsonObject getMainJsonParam(String method) {
        JsonObject json = new JsonObject();
        json.addProperty("jsonrpc", "2.0");
        json.addProperty("method", method);
        json.addProperty("id", 1);

        return json;
    }

    /**
      *@Description TODO lock rewards
      *@param
      *@return txid
    **/
    public static void lockRewardsTransaction(String rewardsName, String rewardsTxid, String num, ApiMethods.RequestListener listener) {
//        TODO: check json body
        JsonArray array = new JsonArray();
        array.add(rewardsName);
        array.add(rewardsTxid);
        array.add(num);

        JsonObject json = getMainJsonParam("rewardslock");
        json.add("params", array);
        Log.d("svcom", "request json: " + json.toString());
        Requestor.sendRawTransaction(json, listener);
    }

    /**
     *@Description TODO unlock rewards
     *@param   txid:lock时所创建的txid
     *@return
     **/
    public static void unlockRewardsTransaction(String rewardsName, String rewardsTxid, String txid, ApiMethods.RequestListener listener) {
//        TODO: check json body
        JsonArray array = new JsonArray();
        array.add(rewardsName);
        array.add(rewardsTxid);
        array.add(txid);

        JsonObject json = getMainJsonParam("rewardsunlock");
        json.add("params", array);
        Log.d("svcom", "request json: " + json.toString());
        Requestor.sendRawTransaction(json, listener);
    }

}
