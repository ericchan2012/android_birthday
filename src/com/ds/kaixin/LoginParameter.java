package com.ds.kaixin;

import android.os.Bundle;
import android.text.TextUtils;


public class LoginParameter extends BaseParameter {
	/**
	 * Ӧ�õ�app id
	 */
	private String mAppId;
	
	/**
	 * Ӧ�õ�secret key
	 */
	private String mAppSecretKey;
	
	/**
	 * �û����û���
	 */
	private String mUserName;
	
	/**
	 * �û������룬����
	 */
	private String mPassword;
	
	/**
	 * �����Ȩ��
	 */
	private String[] mPermission;

	/**
	 * 
	 * @param userName
	 * @param password
	 * @param permissions
	 */
	public LoginParameter (String userName, String password, String[] permissions) {
		this.mUserName 		= userName;
		this.mPassword 		= password;
		this.mPermission 	= permissions;
		mAppId				= Kaixin.API_KEY;
		mAppSecretKey 		= Kaixin.SECRET_KEY;
	}
	
	public void setUserName(String sName){
		mUserName = sName;
	}
	
	public void setPassword(String sPassword){
		mPassword = sPassword;
	}
	
	@Override
	public Bundle getParams() throws KaixinAuthError {
		// TODO Auto-generated method stub
		checkNullParams(mAppId, mAppSecretKey, mUserName, mPassword);

        Bundle param = new Bundle();
        param.putString("grant_type", "password");
        param.putString("username", mUserName);
        param.putString("password", mPassword);
        param.putString("client_id", mAppId);
        param.putString("client_secret", mAppSecretKey);
        if (mPermission != null && mPermission.length > 0) {
            String scope = TextUtils.join(" ", mPermission);
            param.putString("scope", scope);
        }
        
        return param;
	}
}
