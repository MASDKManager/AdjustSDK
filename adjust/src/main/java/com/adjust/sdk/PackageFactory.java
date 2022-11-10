package com.adjust.sdk;

import static com.adjust.sdk.Constants.ENCODING;
import static com.adjust.sdk.Constants.MALFORMED;

import android.net.Uri;
import android.net.UrlQuerySanitizer;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by uerceg on 04.08.17.
 */

public class PackageFactory {
    private static final String ADJUST_PREFIX = "adjust_";

    public static com.adjust.sdk.ActivityPackage buildReftagSdkClickPackage(final String rawReferrer,
                                                                            final long clickTime,
                                                                            final com.adjust.sdk.ActivityState activityState,
                                                                            final com.adjust.sdk.AdjustConfig adjustConfig,
                                                                            final DeviceInfo deviceInfo,
                                                                            final com.adjust.sdk.SessionParameters sessionParameters) {
        if (rawReferrer == null || rawReferrer.length() == 0) {
            return null;
        }

        String referrer;

        try {
            referrer = URLDecoder.decode(rawReferrer, ENCODING);
        } catch (UnsupportedEncodingException e) {
            referrer = MALFORMED;
            AdjustFactory.getLogger().error("Referrer decoding failed due to UnsupportedEncodingException. Message: (%s)", e.getMessage());
        } catch (IllegalArgumentException e) {
            referrer = MALFORMED;
            AdjustFactory.getLogger().error("Referrer decoding failed due to IllegalArgumentException. Message: (%s)", e.getMessage());
        } catch (Exception e) {
            referrer = MALFORMED;
            AdjustFactory.getLogger().error("Referrer decoding failed. Message: (%s)", e.getMessage());
        }

        AdjustFactory.getLogger().verbose("Referrer to parse (%s)", referrer);

        UrlQuerySanitizer querySanitizer = new UrlQuerySanitizer();
        querySanitizer.setUnregisteredParameterValueSanitizer(UrlQuerySanitizer.getAllButNulLegal());
        querySanitizer.setAllowUnregisteredParamaters(true);
        querySanitizer.parseQuery(referrer);

        PackageBuilder clickPackageBuilder = queryStringClickPackageBuilder(
                querySanitizer.getParameterList(),
                activityState,
                adjustConfig,
                deviceInfo,
                sessionParameters);

        if (clickPackageBuilder == null) {
            return null;
        }

        clickPackageBuilder.referrer = referrer;
        clickPackageBuilder.clickTimeInMilliseconds = clickTime;
        clickPackageBuilder.rawReferrer = rawReferrer;

        com.adjust.sdk.ActivityPackage clickPackage = clickPackageBuilder.buildClickPackage(com.adjust.sdk.Constants.REFTAG);

        return clickPackage;
    }

    public static com.adjust.sdk.ActivityPackage buildDeeplinkSdkClickPackage(final Uri url,
                                                                              final long clickTime,
                                                                              final com.adjust.sdk.ActivityState activityState,
                                                                              final com.adjust.sdk.AdjustConfig adjustConfig,
                                                                              final DeviceInfo deviceInfo,
                                                                              final com.adjust.sdk.SessionParameters sessionParameters) {
        if (url == null) {
            return null;
        }

        String urlString = url.toString();

        if (urlString == null || urlString.length() == 0) {
            return null;
        }

        String urlStringDecoded;

        try {
            urlStringDecoded = URLDecoder.decode(urlString, ENCODING);
        } catch (UnsupportedEncodingException e) {
            urlStringDecoded = urlString;
            AdjustFactory.getLogger().error("Deeplink url decoding failed due to UnsupportedEncodingException. Message: (%s)", e.getMessage());
        } catch (IllegalArgumentException e) {
            urlStringDecoded = urlString;
            AdjustFactory.getLogger().error("Deeplink url decoding failed due to IllegalArgumentException. Message: (%s)", e.getMessage());
        } catch (Exception e) {
            urlStringDecoded = urlString;
            AdjustFactory.getLogger().error("Deeplink url decoding failed. Message: (%s)", e.getMessage());
        }

        AdjustFactory.getLogger().verbose("Url to parse (%s)", urlStringDecoded);

        UrlQuerySanitizer querySanitizer = new UrlQuerySanitizer();
        querySanitizer.setUnregisteredParameterValueSanitizer(UrlQuerySanitizer.getAllButNulLegal());
        querySanitizer.setAllowUnregisteredParamaters(true);
        querySanitizer.parseUrl(urlStringDecoded);

        PackageBuilder clickPackageBuilder = queryStringClickPackageBuilder(
                querySanitizer.getParameterList(),
                activityState,
                adjustConfig,
                deviceInfo,
                sessionParameters);

        if (clickPackageBuilder == null) {
            return null;
        }

        clickPackageBuilder.deeplink = url.toString();
        clickPackageBuilder.clickTimeInMilliseconds = clickTime;

        com.adjust.sdk.ActivityPackage clickPackage = clickPackageBuilder.buildClickPackage(com.adjust.sdk.Constants.DEEPLINK);

        return clickPackage;
    }

    public static com.adjust.sdk.ActivityPackage buildInstallReferrerSdkClickPackage(final ReferrerDetails referrerDetails,
                                                                                     final String referrerApi,
                                                                                     final com.adjust.sdk.ActivityState activityState,
                                                                                     final com.adjust.sdk.AdjustConfig adjustConfig,
                                                                                     final DeviceInfo deviceInfo,
                                                                                     final com.adjust.sdk.SessionParameters sessionParameters) {
        long now = System.currentTimeMillis();

        PackageBuilder clickPackageBuilder = new PackageBuilder(
                adjustConfig,
                deviceInfo,
                activityState,
                sessionParameters,
                now);

        clickPackageBuilder.referrer = referrerDetails.installReferrer;
        clickPackageBuilder.clickTimeInSeconds = referrerDetails.referrerClickTimestampSeconds;
        clickPackageBuilder.installBeginTimeInSeconds = referrerDetails.installBeginTimestampSeconds;
        clickPackageBuilder.clickTimeServerInSeconds = referrerDetails.referrerClickTimestampServerSeconds;
        clickPackageBuilder.installBeginTimeServerInSeconds = referrerDetails.installBeginTimestampServerSeconds;
        clickPackageBuilder.installVersion = referrerDetails.installVersion;
        clickPackageBuilder.googlePlayInstant = referrerDetails.googlePlayInstant;
        clickPackageBuilder.referrerApi = referrerApi;

        com.adjust.sdk.ActivityPackage clickPackage = clickPackageBuilder.buildClickPackage(com.adjust.sdk.Constants.INSTALL_REFERRER);

        return clickPackage;
    }

    public static com.adjust.sdk.ActivityPackage buildPreinstallSdkClickPackage(final String preinstallPayload,
                                                                                final String preinstallLocation,
                                                                                final com.adjust.sdk.ActivityState activityState,
                                                                                final com.adjust.sdk.AdjustConfig adjustConfig,
                                                                                final DeviceInfo deviceInfo,
                                                                                final com.adjust.sdk.SessionParameters sessionParameters) {
        if (preinstallPayload == null || preinstallPayload.length() == 0) {
            return null;
        }

        long now = System.currentTimeMillis();

        PackageBuilder clickPackageBuilder = new PackageBuilder(
                adjustConfig,
                deviceInfo,
                activityState,
                sessionParameters,
                now);

        clickPackageBuilder.preinstallPayload = preinstallPayload;
        clickPackageBuilder.preinstallLocation = preinstallLocation;

        ActivityPackage clickPackage = clickPackageBuilder.buildClickPackage(com.adjust.sdk.Constants.PREINSTALL);

        return clickPackage;
    }

    private static PackageBuilder queryStringClickPackageBuilder(
            final List<UrlQuerySanitizer.ParameterValuePair> queryList,
            final ActivityState activityState,
            final AdjustConfig adjustConfig,
            final DeviceInfo deviceInfo,
            final SessionParameters sessionParameters) {
        if (queryList == null) {
            return null;
        }

        Map<String, String> queryStringParameters = new LinkedHashMap<String, String>();
        com.adjust.sdk.AdjustAttribution queryStringAttribution = new com.adjust.sdk.AdjustAttribution();

        for (UrlQuerySanitizer.ParameterValuePair parameterValuePair : queryList) {
            readQueryString(
                    parameterValuePair.mParameter,
                    parameterValuePair.mValue,
                    queryStringParameters,
                    queryStringAttribution);
        }

        long now = System.currentTimeMillis();
        String reftag = queryStringParameters.remove(Constants.REFTAG);

        // Check if activity state != null
        // (referrer can be called before onResume)
        if (activityState != null) {
            long lastInterval = now - activityState.lastActivity;
            activityState.lastInterval = lastInterval;
        }

        PackageBuilder builder = new PackageBuilder(
                adjustConfig,
                deviceInfo,
                activityState,
                sessionParameters,
                now);

        builder.extraParameters = queryStringParameters;
        builder.attribution = queryStringAttribution;
        builder.reftag = reftag;

        return builder;
    }

    private static boolean readQueryString(final String key,
                                           final String value,
                                           final Map<String, String> extraParameters,
                                           com.adjust.sdk.AdjustAttribution queryStringAttribution) {
        if (key == null || value == null) {
            return false;
        }

        // Parameter key does not start with "adjust_" prefix.
        if (!key.startsWith(ADJUST_PREFIX)) {
            return false;
        }

        String keyWOutPrefix = key.substring(ADJUST_PREFIX.length());

        if (keyWOutPrefix.length() == 0) {
            return false;
        }

        if (value.length() == 0) {
            return false;
        }

        if (!tryToSetAttribution(queryStringAttribution, keyWOutPrefix, value)) {
            extraParameters.put(keyWOutPrefix, value);
        }

        return true;
    }

    private static boolean tryToSetAttribution(AdjustAttribution queryStringAttribution,
                                               final String key,
                                               final String value) {
        if (key.equals("tracker")) {
            queryStringAttribution.trackerName = value;
            return true;
        }

        if (key.equals("campaign")) {
            queryStringAttribution.campaign = value;
            return true;
        }

        if (key.equals("adgroup")) {
            queryStringAttribution.adgroup = value;
            return true;
        }

        if (key.equals("creative")) {
            queryStringAttribution.creative = value;
            return true;
        }

        return false;
    }
}
