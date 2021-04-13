package com.plexus.keyvalue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.MutableLiveData;

import com.plexus.core.utils.logging.Log;
import com.plexus.dependecies.PlexusDependencies;
import com.plexus.utils.Util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class PlexusPayValues extends PlexusStoreValues{

    private static final String TAG = Log.tag(PlexusPayValues.class);

    private static final String PLEXUS_PAY_ENABLED =  "plexus_pay_enabled";
    private static final String PLEXUS_PAY_CURRENT_CURRENCY = "plexus_pay_current_currency";
    private static final String DEFAULT_CURRENCY_CODE     = "ZAR";

    /**
     * Shipping Information
     */
    private static final String PLEXUS_PAY_USER_NAME =  "plexus_pay_user_name";
    private static final String PLEXUS_PAY_SHIPPING_ADDRESS     = "plexus_pay_shipping_address";
    private static final String PLEXUS_PAY_CONTACT_EMAIL =  "plexus_pay_enabled";
    private static final String PLEXUS_PAY_CONTACT_NUMBER = "plexus_pay_current_currency";

    /**
     * Shipping Information
     */
    private static final String PLEXUS_PAY_DEFAULT_PAYMENT_METHOD = "plexus_pay_default_payment_method";

    private final MutableLiveData<Currency> liveCurrentCurrency;

    PlexusPayValues(@NonNull @NotNull KeyValueStore store) {
        super(store);
        this.liveCurrentCurrency   = new MutableLiveData<>(currentCurrency());
    }

    @Override
    void onFirstEverAppLaunch() {

    }

    @NonNull
    @NotNull
    @Override
    List<String> getKeysToIncludeInBackup() {
        return Arrays.asList(PLEXUS_PAY_ENABLED,
                PLEXUS_PAY_CURRENT_CURRENCY,
                PLEXUS_PAY_CONTACT_EMAIL,
                PLEXUS_PAY_CONTACT_NUMBER,
                PLEXUS_PAY_DEFAULT_PAYMENT_METHOD,
                PLEXUS_PAY_SHIPPING_ADDRESS,
                PLEXUS_PAY_USER_NAME,
                DEFAULT_CURRENCY_CODE);
    }

    public boolean plexusPayEnabled(){
        KeyValueReader reader = getStore().beginRead();
        return reader.getBoolean(PLEXUS_PAY_ENABLED, false);
    }

    @WorkerThread
    public void setPlexusPayEnabled(boolean isPlexusPayEnabled){
        if (plexusPayEnabled() == isPlexusPayEnabled){
            return;
        }

        if (isPlexusPayEnabled){
            getStore().beginWrite()
                    .putBoolean(PLEXUS_PAY_ENABLED, true)
                    .putString(PLEXUS_PAY_CURRENT_CURRENCY, "")
                    .commit();
        } else {
            getStore().beginWrite()
                    .putBoolean(PLEXUS_PAY_ENABLED, false)
                    .commit();
        }
    }

    public void setCurrentCurrency(@NonNull Currency currentCurrency) {
        getStore().beginWrite()
                .putString(PLEXUS_PAY_CURRENT_CURRENCY, currentCurrency.getCurrencyCode())
                .commit();

        liveCurrentCurrency.postValue(currentCurrency);
    }

    public @NonNull Currency currentCurrency() {
        String currencyCode = getStore().getString(PLEXUS_PAY_CURRENT_CURRENCY, null);
        return currencyCode == null ? determineCurrency()
                : Currency.getInstance(currencyCode);
    }

    public @NonNull
    MutableLiveData<Currency> liveCurrentCurrency() {
        return liveCurrentCurrency;
    }

    private @NonNull Currency determineCurrency() {
        String localE164 = TextSecurePreferences.getLocalNumber(PlexusDependencies.getApplication());
        if (localE164 == null) {
            localE164 = "";
        }
        return Util.firstNonNull(CurrencyUtil.getCurrencyByE164(localE164),
                CurrencyUtil.getCurrencyByLocale(Locale.getDefault()),
                Currency.getInstance(DEFAULT_CURRENCY_CODE));
    }

    public static @Nullable Currency getCurrencyByLocale(@Nullable Locale locale) {
        if (locale == null) {
            return null;
        }

        try {
            return Currency.getInstance(locale);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void setPlexusPayContactEmail(String value) {
        getStore().beginWrite()
                .putString(PLEXUS_PAY_CONTACT_EMAIL, value)
                .commit();
    }

    public void setPlexusPayContactNumber(String value) {
        getStore().beginWrite()
                .putString(PLEXUS_PAY_CONTACT_NUMBER, value)
                .commit();
    }

    public void setPlexusPayShippingAddress(String value) {
        getStore().beginWrite()
                .putString(PLEXUS_PAY_SHIPPING_ADDRESS, value)
                .commit();
    }

    public void setPlexusPayUserName(String value) {
        getStore().beginWrite()
                .putString(PLEXUS_PAY_USER_NAME, value)
                .commit();
    }

    public void setPlexusPayDefaultPaymentMethod(String value) {
        getStore().beginWrite()
                .putString(PLEXUS_PAY_DEFAULT_PAYMENT_METHOD, value)
                .commit();
    }

    public void getPlexusPayContactEmail() {
        getStore().beginRead()
                .getString(PLEXUS_PAY_CONTACT_EMAIL, "");
    }

    public void getPlexusPayContactNumber() {
        getStore().beginRead()
                .getString(PLEXUS_PAY_CONTACT_NUMBER, "");
    }

    public void getPlexusPayShippingAddress() {
        getStore().beginRead()
                .getString(PLEXUS_PAY_SHIPPING_ADDRESS, "");
    }

    public void getPlexusPayUserName() {
        getStore().beginRead()
                .getString(PLEXUS_PAY_USER_NAME, "");
    }

    public void getPlexusPayDefaultPaymentMethod() {
        getStore().beginRead()
                .getString(PLEXUS_PAY_DEFAULT_PAYMENT_METHOD, "");
    }

}
