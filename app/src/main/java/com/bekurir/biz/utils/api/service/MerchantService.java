package com.bekurir.biz.utils.api.service;

import com.bekurir.biz.json.ActiveCatRequestJson;
import com.bekurir.biz.json.AddEditItemRequestJson;
import com.bekurir.biz.json.AddEditKategoriRequestJson;
import com.bekurir.biz.json.BankResponseJson;
import com.bekurir.biz.json.ChangePassRequestJson;
import com.bekurir.biz.json.DetailRequestJson;
import com.bekurir.biz.json.DetailTransResponseJson;
import com.bekurir.biz.json.EditMerchantRequestJson;
import com.bekurir.biz.json.EditProfileRequestJson;
import com.bekurir.biz.json.GetServiceResponseJson;
import com.bekurir.biz.json.GetOnRequestJson;
import com.bekurir.biz.json.HistoryRequestJson;
import com.bekurir.biz.json.HistoryResponseJson;
import com.bekurir.biz.json.HomeRequestJson;
import com.bekurir.biz.json.HomeResponseJson;
import com.bekurir.biz.json.ItemRequestJson;
import com.bekurir.biz.json.ItemResponseJson;
import com.bekurir.biz.json.CategoryRequestJson;
import com.bekurir.biz.json.CategoryResponseJson;
import com.bekurir.biz.json.LoginRequestJson;
import com.bekurir.biz.json.LoginResponseJson;
import com.bekurir.biz.json.PrivacyRequestJson;
import com.bekurir.biz.json.PrivacyResponseJson;
import com.bekurir.biz.json.RegisterRequestJson;
import com.bekurir.biz.json.RegisterResponseJson;
import com.bekurir.biz.json.ResponseJson;
import com.bekurir.biz.json.StripeRequestJson;
import com.bekurir.biz.json.TopupRequestJson;
import com.bekurir.biz.json.TopupResponseJson;
import com.bekurir.biz.json.WalletRequestJson;
import com.bekurir.biz.json.WalletResponseJson;
import com.bekurir.biz.json.WithdrawRequestJson;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Ourdevelops Team on 10/13/2019.
 */

public interface MerchantService {

    @GET("partnerapi/kategorimerchant")
    Call<GetServiceResponseJson> getFitur();

    @POST("customerapi/list_bank")
    Call<BankResponseJson> listbank(@Body WithdrawRequestJson param);

    @POST("partnerapi/kategorimerchantbyfitur")
    Call<GetServiceResponseJson> getKategori(@Body HistoryRequestJson param);

    @POST("partnerapi/onoff")
    Call<ResponseJson> turnon(@Body GetOnRequestJson param);

    @POST("partnerapi/login")
    Call<LoginResponseJson> login(@Body LoginRequestJson param);

    @POST("partnerapi/register_merchant")
    Call<RegisterResponseJson> register(@Body RegisterRequestJson param);

    @POST("partnerapi/forgot")
    Call<LoginResponseJson> forgot(@Body LoginRequestJson param);

    @POST("customerapi/privacy")
    Call<PrivacyResponseJson> privacy(@Body PrivacyRequestJson param);

    @POST("partnerapi/edit_profile")
    Call<LoginResponseJson> editprofile(@Body EditProfileRequestJson param);

    @POST("partnerapi/edit_merchant")
    Call<LoginResponseJson> editmerchant(@Body EditMerchantRequestJson param);

    @POST("partnerapi/home")
    Call<HomeResponseJson> home(@Body HomeRequestJson param);

    @POST("partnerapi/history")
    Call<HistoryResponseJson> history(@Body HistoryRequestJson param);

    @POST("partnerapi/detail_transaksi")
    Call<DetailTransResponseJson> detailtrans(@Body DetailRequestJson param);

    @POST("partnerapi/category")
    Call<CategoryResponseJson> category(@Body CategoryRequestJson param);

    @POST("partnerapi/item")
    Call<ItemResponseJson> itemlist(@Body ItemRequestJson param);

    @POST("partnerapi/active_kategori")
    Call<ResponseJson> activekategori(@Body ActiveCatRequestJson param);

    @POST("partnerapi/active_item")
    Call<ResponseJson> activeitem(@Body ActiveCatRequestJson param);

    @POST("partnerapi/add_kategori")
    Call<ResponseJson> addkategori(@Body AddEditKategoriRequestJson param);

    @POST("partnerapi/edit_kategori")
    Call<ResponseJson> editkategori(@Body AddEditKategoriRequestJson param);

    @POST("partnerapi/delete_kategori")
    Call<ResponseJson> deletekategori(@Body AddEditKategoriRequestJson param);

    @POST("partnerapi/add_item")
    Call<ResponseJson> additem(@Body AddEditItemRequestJson param);

    @POST("partnerapi/edit_item")
    Call<ResponseJson> edititem(@Body AddEditItemRequestJson param);

    @POST("partnerapi/delete_item")
    Call<ResponseJson> deleteitem(@Body AddEditItemRequestJson param);

    @POST("customerapi/topupstripe")
    Call<TopupResponseJson> topup(@Body TopupRequestJson param);

    @POST("partnerapi/withdraw")
    Call<ResponseJson> withdraw(@Body WithdrawRequestJson param);

    @POST("customerapi/wallet")
    Call<WalletResponseJson> wallet(@Body WalletRequestJson param);

    @POST("partnerapi/topuppaypal")
    Call<ResponseJson> topuppaypal(@Body WithdrawRequestJson param);

    @POST("partnerapi/changepass")
    Call<LoginResponseJson> changepass(@Body ChangePassRequestJson param);

    @POST("partnerapi/stripeaction")
    Call<ResponseJson> actionstripe(@Body StripeRequestJson param);

    @POST("partnerapi/intentstripe")
    Call<ResponseJson> intentstripe(@Body StripeRequestJson param);

}
