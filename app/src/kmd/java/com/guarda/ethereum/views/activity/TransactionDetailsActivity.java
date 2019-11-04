package com.guarda.ethereum.views.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.guarda.ethereum.GuardaApp;
import com.guarda.ethereum.R;
import com.guarda.ethereum.managers.EthereumNetworkManager;
import com.guarda.ethereum.managers.TransactionsManager;
import com.guarda.ethereum.managers.WalletManager;
import com.guarda.ethereum.models.constants.Common;
import com.guarda.ethereum.models.constants.Extras;
import com.guarda.ethereum.models.items.TransactionItem;
import com.guarda.ethereum.utils.ClipboardUtils;
import com.guarda.ethereum.views.activity.base.AToolbarMenuActivity;

import org.bitcoinj.core.Coin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import autodagger.AutoInjector;
import butterknife.BindView;
import butterknife.OnClick;

import static com.guarda.ethereum.models.constants.Common.EXTRA_TRANSACTION_POSITION;

@AutoInjector(GuardaApp.class)
public class TransactionDetailsActivity extends AToolbarMenuActivity {

    @BindView(R.id.et_trans_details_sum)
    EditText etTrValue;

    @BindView(R.id.et_trans_details_date)
    EditText etDate;

    @BindView(R.id.et_trans_details_time)
    EditText etTime;

    @BindView(R.id.et_trans_details_hash)
    EditText etHash;
    /*后添加的发送和接受*/
    @BindView(R.id.et_trans_details_balance_sender)
    EditText etBalanceSender;

    @BindView(R.id.et_trans_details_balance_receiver)
    EditText etBalanceReceiver;

    @BindView(R.id.et_trans_details_balance_after)
    EditText etBalanceAfter;

    @BindView(R.id.et_trans_details_balance_before)
    EditText etBalanceBefore;

    @BindView(R.id.btn_copy)
    Button btnCopy;

    @BindView(R.id.btn_repeat)
    Button btnRepeat;

    @BindView(R.id.tv_confirmations)
    TextView etConfirmations;

    @Inject
    TransactionsManager transactionsManager;

    @Inject
    WalletManager walletManager;

    @Inject
    EthereumNetworkManager networkManager;

    private static final String BLOCK_EXPLORER_URL = Common.KMD_EXPLORER_BASE_URL+"/tx/";
    private static final String BLOCK_EXPLORER_URL_01 = Common.KMD_EXPLORER_BASE_URL_01+"/tx/";
    private TransactionItem transaction;

    @Override
    protected void init(Bundle savedInstanceState) {
        GuardaApp.getAppComponent().inject(this);
        setToolBarTitle(getString(R.string.title_transaction_detail));
        int transactionPosition = getIntent().getIntExtra(EXTRA_TRANSACTION_POSITION, 0);
        transaction = transactionsManager.getTxByPosition(transactionPosition);
        updateViews();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_transaction_details;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.no_slide, R.anim.slide_in_right);
    }

    private void updateViews() {
        if (transaction != null) {
            setValue(transaction.isOut() ? -transaction.getValue() : transaction.getValue());
            setDateAndTime();
            setHash(transaction.getHash());
            setConfirmations(String.valueOf(transaction.getConfirmations()));
            setBalanceDetails();
            initRepeatButton();
        }
    }

    private void initRepeatButton() {
        if (!transaction.isOut()) {
            btnRepeat.setVisibility(View.GONE);
        } else {
            btnRepeat.setVisibility(View.VISIBLE);
        }
    }


    private void setBalanceDetails() {
        long balanceBefore = transactionsManager.getBalanceByTime(true, walletManager.getMyBalance().getValue(), transaction.getTime());
        long balanceAfter;
        if (balanceBefore == 0) {
            balanceAfter = getStartBalanceAfter(balanceBefore, transaction.getValue());
        } else {
            balanceAfter = transactionsManager.getBalanceByTime(false, walletManager.getMyBalance().getValue(), transaction.getTime());
        }
        etBalanceBefore.setText(String.format("%s %s", WalletManager.getFriendlyBalance(Coin.valueOf(balanceBefore)), Common.MAIN_CURRENCY.toUpperCase()));
        etBalanceAfter.setText(String.format("%s %s", WalletManager.getFriendlyBalance(Coin.valueOf(balanceAfter)), Common.MAIN_CURRENCY.toUpperCase()));
        /**
          *@Description TODO sender receiver
          *@param
          *@return
        **/
        etBalanceSender.setText(transaction.getFrom());
        etBalanceReceiver.setText(transaction.getTo());
    }

    private long getStartBalanceAfter(long balanceBefore, long summ) {
        return balanceBefore + summ;
    }


    private void setConfirmations(String confirmations) {
        if (confirmations != null) {
            etConfirmations.setText(confirmations);
        } else {
            etConfirmations.setText(String.format(Locale.US, "%d", 0));
        }
    }

    private void setHash(String hash) {
        etHash.setText(hash);
    }

    private void setDateAndTime() {
        Date date = new Date(transaction.getTime() * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        etDate.setText(dateFormat.format(date));
        etTime.setText(timeFormat.format(date));
    }

    private void setValue(long value) {
        Coin coin = Coin.valueOf(value);
        etTrValue.setText(String.format("%s %s", WalletManager.getFriendlyBalance(coin), Common.MAIN_CURRENCY.toUpperCase()));
    }

    private boolean isDebit(String ourAddress, String toAddress) {
        return ourAddress.equals(toAddress);
    }

    @OnClick(R.id.btn_copy)
    public void onCopyClick() {
        ClipboardUtils.copyToClipBoard(this, transaction.getHash());
    }

    @OnClick(R.id.btn_repeat)
    public void onRepeatTransactionClick() {

        String trValue = etTrValue.getText().toString();
        if (trValue.charAt(0) == '-') {
            trValue = trValue.substring(1);
        }

        Intent intent = new Intent(this, SendingCurrencyActivity.class);
        intent.putExtra(Extras.WALLET_NUMBER, transaction.getTo());
        intent.putExtra(Extras.AMOUNT_TO_SEND, WalletManager.getFriendlyBalance(Coin.valueOf(transaction.getValue())));
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.no_slide);
    }

    @OnClick({R.id.til_trans_details_hash, R.id.et_trans_details_hash})
    public void onTransHashClick() {
        String[] items = new String[]{"主服务器","备用服务器"};
        String hashDetailUrl = BLOCK_EXPLORER_URL + etHash.getText().toString();
        String hashDetailUrl01 = BLOCK_EXPLORER_URL_01 + etHash.getText().toString();
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("请选择服务器")
                .setItems(items, new DialogInterface.OnClickListener() {//添加列表
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Toast.makeText(AlertDialogActivity.this, "点的是：" + items3[i], Toast.LENGTH_SHORT).show();
                        if (i == 0){
                            openWebURL(hashDetailUrl);
                            Log.d("hashDetailUrl", "onClick: "+hashDetailUrl);
                        } else {
                            openWebURL(hashDetailUrl01);
                            Log.d("hashDetailUrl01", "onClick: "+hashDetailUrl01);
                        }

                    }
                })
                .create();
        alertDialog.show();

    }

    public void openWebURL(String inURL) {
        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(inURL));
        startActivity(browse);

    }

}
