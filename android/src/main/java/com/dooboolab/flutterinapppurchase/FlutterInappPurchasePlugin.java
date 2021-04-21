package com.dooboolab.flutterinapppurchase;

import android.content.Context;
import android.content.pm.PackageManager;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** FlutterInappPurchasePlugin */
public class FlutterInappPurchasePlugin implements FlutterPlugin, ActivityAware {

  private AndroidInappPurchasePlugin androidInappPurchasePlugin;
  private Context context;
  private MethodChannel channel;

  private static boolean isAndroid;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
    context = binding.getApplicationContext();
    isAndroid = isPackageInstalled(context, "com.android.vending");

    if (isAndroid) {
      androidInappPurchasePlugin = new AndroidInappPurchasePlugin();
      androidInappPurchasePlugin.setContext(context);

      setupMethodChannel(binding.getBinaryMessenger(), androidInappPurchasePlugin);

    } 
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    if (isPackageInstalled(context, "com.android.vending")) {
      tearDownChannel();
    }
  }

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    FlutterInappPurchasePlugin plugin = new FlutterInappPurchasePlugin();
    if(isAndroid) {
      AndroidInappPurchasePlugin androidInappPurchasePlugin = new AndroidInappPurchasePlugin();
      androidInappPurchasePlugin.setContext(registrar.context());
      androidInappPurchasePlugin.setActivity(registrar.activity());

      plugin.setupMethodChannel(registrar.messenger(), androidInappPurchasePlugin);
      plugin.setAndroidInappPurchasePlugin(androidInappPurchasePlugin);
    }
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    if (isPackageInstalled(context, "com.android.vending")) {
      androidInappPurchasePlugin.setActivity(binding.getActivity());
    }
  }

  @Override
  public void onDetachedFromActivity() {
    if (isPackageInstalled(context, "com.android.vending")) {
      androidInappPurchasePlugin.setActivity(null);
      androidInappPurchasePlugin.onDetachedFromActivity();
    }
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    onAttachedToActivity(binding);
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity();
  }

  private static boolean isPackageInstalled(Context ctx, String packageName) {
    try {
      ctx.getPackageManager().getPackageInfo(packageName, 0);
    } catch (PackageManager.NameNotFoundException e) {
      return false;
    }
    return true;
  }

  private void setupMethodChannel(BinaryMessenger messenger, MethodChannel.MethodCallHandler handler) {
    channel = new MethodChannel(messenger, "flutter_inapp");
    channel.setMethodCallHandler(handler);
    setChannelByPlatform(channel);
  }

  private void tearDownChannel() {
    channel.setMethodCallHandler(null);
    channel = null;
    setChannelByPlatform(null);
  }

  private void setChannelByPlatform(MethodChannel channel) {
    if(isAndroid) {
      androidInappPurchasePlugin.setChannel(channel);
    }
  }

  private void setAndroidInappPurchasePlugin(AndroidInappPurchasePlugin androidInappPurchasePlugin) {
    this.androidInappPurchasePlugin = androidInappPurchasePlugin;
  }

}
