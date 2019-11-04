package com.guarda.ethereum.views.fragments;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import autodagger.AutoInjector;
import butterknife.BindView;
import butterknife.OnClick;
import com.gravilink.zcash.WalletCallback;
import com.gravilink.zcash.ZCashException;
import com.gravilink.zcash.ZCashTransaction_taddr;
import com.gravilink.zcash.ZCashWalletManager;
import com.gravilink.zcash.crypto.Utils;
import com.guarda.ethereum.GuardaApp;
import com.guarda.ethereum.R;
import com.guarda.ethereum.managers.*;
import com.guarda.ethereum.models.items.SendRawTxResponse;
import com.guarda.ethereum.rest.ApiMethods;
import com.guarda.ethereum.utils.CurrencyUtils;
import com.guarda.ethereum.views.activity.MainActivity;
import com.guarda.ethereum.views.activity.SendingCurrencyActivity;
import com.guarda.ethereum.views.fragments.base.BaseFragment;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.WrongNetworkException;

import javax.inject.Inject;

import static com.guarda.ethereum.models.constants.Common.KMD_MIN_CONFIRM;

/**
 * @ClassName RewardsFragment
 * @Description TODO
 * @Author songbo
 * @Date 19-9-12 下午6:02
 **/
@AutoInjector(GuardaApp.class)
public class RewardsFragment extends BaseFragment {

    @BindView(R.id.btn_confirm_rewards)
    Button btnRewards;


    private static final String BLANK_BALANCE = "...";
    private boolean isVisible = true;
    private boolean stronglyHistory = false;
    private Handler handler = new Handler();
    private ObjectAnimator loaderAnimation;

    private long curInterest = 0;

    @Inject
    WalletManager walletManager;

    @Inject
    EthereumNetworkManager networkManager;

    @Inject
    TransactionsManager transactionsManager;

    @Inject
    SharedManager sharedManager;

    @Inject
    RawNodeManager mNodeManager;

    Fragment thisFragment = this;

    private long currentFeeEth;

    @Override
    protected int getLayout() {
        return R.layout.fragment_rewards;
    }

    @Override
    protected void init() {

        GuardaApp.getAppComponent().inject(this);

        Coin defaultFee = Coin.valueOf(164000);
        currentFeeEth = defaultFee.getValue();



    }
    @OnClick(R.id.btn_confirm_rewards)
    public void onConfirmClick(View view) {
        try {
            String amount = "2000000000";
            if (!amount.isEmpty()) {
                showProgress();
                //long amountSatoshi = Coin.parseCoin(getAmountToSend(2000000000)).getValue();
                //Log.d("svcom", "amount=" + amountSatoshi + " fee=" + currentFeeEth);
                // Here is call from Zcash library for supporting Sapling update, because Komodo is Zcash's fork
                ZCashWalletManager.getInstance().createRewardsTransaction_taddr(walletManager.getWalletFriendlyAddress(),
                        "RTsRBYL1HSvMoE3qtBJkyiswdVaWkm8YTK",
                        200000000L,
                        currentFeeEth,
                        walletManager.anyToWif(walletManager.getPrivateKey()),
                        KMD_MIN_CONFIRM,
                        new WalletCallback<String, ZCashTransaction_taddr>()  {
                            @Override
                            public void onResponse(String r1, ZCashTransaction_taddr r2) {
                                Log.i("RESPONSE CODE", r1);
                                if (r1.equals("ok")) {
                                    try {
                                        String lastTxhex = Utils.bytesToHex(r2.getBytes());
                                        Log.i("lastTxhex", lastTxhex);
                                        BitcoinNodeManager.sendTransaction(lastTxhex, new ApiMethods.RequestListener() {
                                            @Override
                                            public void onSuccess(Object response) {
                                                SendRawTxResponse res = (SendRawTxResponse) response;
                                                Log.d("TX_RES", "res " + res.getHashResult() + " error " + res.getError());
                                                closeProgress();
                                                //showCongratsActivity();
                                            }
                                            @Override
                                            public void onFailure(String msg) {
                                                closeProgress();
                                                //doToast(CurrencyUtils.getBtcLikeError(msg));
                                                Log.d("svcom", "failure - " + msg);
                                            }
                                        });
                                    } catch (ZCashException e) {
                                        closeProgress();

                                        Log.i("TX", "Cannot sign transaction");
                                    }
                                } else {
                                    closeProgress();

                                    Log.i("psd", "createTransaction_taddr: RESPONSE CODE is not ok");
                                }

                        }
            });
            }
            } catch (WrongNetworkException wne) {
                closeProgress();
                Log.e("psd", wne.toString());
                //Toast.makeText(this, R.string.send_wrong_address, Toast.LENGTH_SHORT).show();
            } catch (ZCashException zce) {
                closeProgress();
                //Toast.makeText(SendingCurrencyActivity.this, "Error: " + zce.getMessage(), Toast.LENGTH_SHORT).show();
                zce.printStackTrace();
            } catch (Exception e) {
                closeProgress();
                //Toast.makeText(SendingCurrencyActivity.this, "Error of sending", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

    private String getStringIfAdded(int resId) {
        if (isAdded()) {
            return getString(resId);
        } else {
            return "";
        }
    }
    private String getAmountToSend(long send) {
        return String.valueOf(send*0.00164);
    }
}
