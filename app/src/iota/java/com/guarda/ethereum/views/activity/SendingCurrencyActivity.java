package com.guarda.ethereum.views.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.guarda.ethereum.GuardaApp;
import com.guarda.ethereum.R;
import com.guarda.ethereum.managers.BitcoinNodeManager;
import com.guarda.ethereum.managers.Callback;
import com.guarda.ethereum.managers.EthereumNetworkManager;
import com.guarda.ethereum.managers.TransactionsManager;
import com.guarda.ethereum.managers.WalletManager;
import com.guarda.ethereum.models.constants.Common;
import com.guarda.ethereum.models.constants.Extras;
import com.guarda.ethereum.models.items.SendRawTxResponse;
import com.guarda.ethereum.rest.ApiMethods;
import com.guarda.ethereum.utils.DigitsInputFilter;
import com.guarda.ethereum.views.activity.base.AToolbarMenuActivity;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.WrongNetworkException;
import org.iota.wallet.api.responses.SendTransferResponse;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.IllegalFormatConversionException;

import javax.inject.Inject;

import autodagger.AutoInjector;
import butterknife.BindView;
import butterknife.OnClick;

@AutoInjector(GuardaApp.class)
public class SendingCurrencyActivity extends AToolbarMenuActivity {

    @BindView(R.id.et_sum_send)
    EditText etSumSend;
    @BindView(R.id.et_fee_amount)
    EditText etFeeAmount;
    @BindView(R.id.et_arrival_amount)
    EditText etArrivalAmount;
    @BindView(R.id.et_send_coins_address)
    EditText etWalletAddress;
    @BindView(R.id.btn_include)
    Button btnInclude;
    @BindView(R.id.btn_exclude)
    Button btnExclude;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    //    @BindView(R.id.ll_gas_container)
//    LinearLayout gasContainer;
    @BindView(R.id.ll_fee_container)
    LinearLayout feeContainer;
//    @BindView(R.id.switch_gas_fee)
//    SwitchCompat swGasFee;
//    @BindView(R.id.et_gas_limit)
//    EditText etGasLimit;
//    @BindView(R.id.et_custom_data)
//    EditText etCustomData;
//    @BindView(R.id.cl_switch_container)
//    ConstraintLayout switchContainer;
//    @BindView(R.id.tv_gas)
//    TextView tvGas;
//    @BindView(R.id.tv_fee)
//    TextView tvFee;

//    private BigDecimal balance = new BigDecimal(0);

    @Inject
    WalletManager walletManager;

    @Inject
    EthereumNetworkManager networkManager;

    @Inject
    TransactionsManager transactionsManager;

    private String walletNumber;
    private String amountToSend;
    private boolean isInclude = false;
    private long currentFeeEth;
    private String arrivalAmountToSend;


    @Override
    protected void init(Bundle savedInstanceState) {
        GuardaApp.getAppComponent().inject(this);
        setToolBarTitle(getString(R.string.title_withdraw3));
        etSumSend.setFilters(new InputFilter[]{new DigitsInputFilter(8, 8, Float.POSITIVE_INFINITY)});
        etFeeAmount.setFilters(new InputFilter[]{new DigitsInputFilter(8, 8, Float.POSITIVE_INFINITY)});
        walletNumber = getIntent().getStringExtra(Extras.WALLET_NUMBER);
        amountToSend = getIntent().getStringExtra(Extras.AMOUNT_TO_SEND);
        checkBtnIncludeStatus(isInclude);
        setDataToView();
        initSendSumField();
        initFeeField();
        btnInclude.setVisibility(View.GONE);
        btnExclude.setVisibility(View.GONE);
        etFeeAmount.setEnabled(false);
        feeContainer.setVisibility(View.GONE);
//        updateArrivalField();
//        updateWarnings();
//        updateArrivalField();
    }

    private void initSendSumField() {
        etSumSend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hideError(etSumSend);
            }

            @Override
            public void afterTextChanged(Editable s) {
//                updateArrivalField();
//                updateWarnings();
//                updateArrivalField();
            }
        });
    }

    private void initFeeField() {
        Coin defaultFee = Coin.valueOf(0);
        currentFeeEth = defaultFee.getValue();
        etFeeAmount.setText(defaultFee.toPlainString());
        updateArrivalField();
        etFeeAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hideError(etFeeAmount);
            }

            @Override
            public void afterTextChanged(Editable s) {
//                updateArrivalField();
//                updateWarnings();
//                updateArrivalField();
            }
        });
    }

    private void updateWarnings() {
        try {
            String newAmount = etSumSend.getText().toString();
            if (newAmount.length() > 0) {
                findViewById(R.id.eth_hint_sum).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.eth_hint_sum).setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(newAmount)) {
                if (!isAmountMoreBalance(newAmount)) {
                    hideError(etSumSend);
                    amountToSend = newAmount;
                    updateArrivalField();
                } else {
                    showError(etSumSend, getString(R.string.withdraw_amount_more_than_balance));
                }
            } else {
                showError(etSumSend, getString(R.string.withdraw_amount_can_not_be_empty));
            }

            String newFee = etFeeAmount.getText().toString();
            if (newFee.length() > 0) {
                findViewById(R.id.eth_hint_fee).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.eth_hint_fee).setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(newFee)) {
                try {
                    currentFeeEth = Coin.parseCoin(etFeeAmount.getText().toString()).getValue();
                    if (currentFeeEth > 0) {
                        btnConfirm.setEnabled(true);
                    } else {
                        btnConfirm.setEnabled(false);
                        showError(etFeeAmount, getString(R.string.et_error_fee_is_empty));
                    }
                } catch (IllegalFormatConversionException e) {
                    btnConfirm.setEnabled(false);
                }
                if (feeLessThanAmountToSend(newFee)
                        && currentFeeEth > 0) {
                    hideError(etFeeAmount);
                    btnConfirm.setEnabled(true);
                } else if (isInclude) {
                    btnConfirm.setEnabled(false);
                    showError(etFeeAmount, getString(R.string.et_error_fee_more_than_amount));
                }
            } else {
                btnConfirm.setEnabled(false);
                showError(etFeeAmount, getString(R.string.et_error_fee_is_empty));
            }

            long amountSatoshi = Coin.parseCoin(getAmountToSend()).getValue();
            if (amountSatoshi < 0) {
                btnConfirm.setEnabled(false);
                showError(etFeeAmount, getString(R.string.et_error_fee_more_than_amount));
            }
        } catch (Exception e) {
            btnConfirm.setEnabled(false);
            showError(etFeeAmount, getString(R.string.withdraw_amount_can_not_be_empty));
        }
    }

    private void checkFeeLessAmount() {
        if (!feeLessThanAmountToSend(etFeeAmount.getText().toString()) && isInclude) {
            showError(etFeeAmount, getString(R.string.et_error_fee_more_than_amount));
            btnConfirm.setEnabled(false);
        } else {
            hideError(etFeeAmount);
            btnConfirm.setEnabled(true);
        }
    }

    private boolean feeLessThanAmountToSend(String newFee) {
//        BigDecimal feeDecimal = new BigDecimal(newFee);
//        BigDecimal amountDecimal = new BigDecimal(etSumSend.getText().toString());
//        return amountDecimal.compareTo(feeDecimal) > 0;
        return !false;
    }

    private void updateArrivalField() {
        Log.d("flint", "SendingCurrencyActivity.updateArrivalField()...");
        etArrivalAmount.setText(etSumSend.getText().toString());
    }

    private void setDataToView() {
        etSumSend.setText(amountToSend);
        etWalletAddress.setText(walletNumber);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_sending_currency;
    }

    @OnClick({R.id.btn_include, R.id.btn_exclude})
    public void sendingCurrencyButtonClick(View view) {
        switch (view.getId()) {
            case R.id.btn_include:
                isInclude = true;
                checkFeeLessAmount();
                checkBtnIncludeStatus(isInclude);
                updateArrivalField();
                break;
            case R.id.btn_exclude:
                isInclude = false;
                checkFeeLessAmount();
                updateArrivalField();
                checkBtnIncludeStatus(isInclude);
                break;
        }
    }

    private void checkBtnIncludeStatus(boolean isInclude) {
        if (isInclude){
            btnExclude.setBackground(getResources().getDrawable(R.drawable.btn_enable_gray));
            btnInclude.setBackground(getResources().getDrawable(R.drawable.btn_border_blue));
        } else {
            btnInclude.setBackground(getResources().getDrawable(R.drawable.btn_enable_gray));
            btnExclude.setBackground(getResources().getDrawable(R.drawable.btn_border_blue));
        }
    }

    private String getToAddress() {
        return etWalletAddress.getText().toString();
    }

    private String getAmountToSend() {
//        if (isInclude)
//            return etArrivalAmount.getText().toString();
//        else
            return etSumSend.getText().toString();
    }

    @OnClick(R.id.btn_confirm)
    public void onConfirmClick(View view) {
        try {
            String amount = etSumSend.getText().toString();
            if (!amount.isEmpty()) {
                if (!isAmountMoreBalance(amount)) {
                    long amountSatoshi = Coin.parseCoin(getAmountToSend()).getValue();

                    Log.d("svcom", "fee = " + currentFeeEth);
                    showProgress(getString(R.string.progress_bar_sending_transaction));
                    walletManager.sendCoins(getToAddress(), amountSatoshi, new Callback<SendTransferResponse>() {
                        @Override
                        public void onResponse(SendTransferResponse response) {
                            try {
                                runOnUiThread(() -> {
                                    try {
                                        closeProgress();
                                        if (response != null && response.getSuccessfully() != null && Arrays.asList(response.getSuccessfully()).contains(true))
                                            showCongratsActivity();
                                        else
                                            Toast.makeText(SendingCurrencyActivity.this, "Send error", Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    showError(etSumSend, getString(R.string.withdraw_amount_more_than_balance));
                }
            } else {
                showError(etSumSend, getString(R.string.withdraw_amount_can_not_be_empty));
            }
        } catch (WrongNetworkException wne) {
            Log.e("psd", wne.toString());
            String msg = String.format(getString(R.string.send_wrong_address), Common.MAIN_CURRENCY.toUpperCase());
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(SendingCurrencyActivity.this, "Can not send the transaction to the node", Toast.LENGTH_SHORT).show();
            closeProgress();
            e.printStackTrace();
        }
    }

    private boolean isAmountMoreBalance(String amount) {
        return false;
    }

    private void showCongratsActivity() {
        Intent intent = new Intent(this, CongratsActivity.class);
        intent.putExtra(Extras.CONGRATS_TEXT, getString(R.string.result_transaction_sent));
        intent.putExtra(Extras.COME_FROM, Extras.FROM_WITHDRAW);
        startActivity(intent);
    }

}
