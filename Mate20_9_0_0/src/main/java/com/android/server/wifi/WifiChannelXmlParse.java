package com.android.server.wifi;

import android.util.Log;
import android.util.Xml;
import huawei.cust.HwCfgFilePolicy;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class WifiChannelXmlParse {
    private static final String CUST_CONFIG_NAME = "xml/wifi_channel_config_cust.xml";
    private static boolean DBG = HWFLOW;
    private static final String DEFAULT_CONFIG_NAME = "xml/wifi_channel_config.xml";
    protected static final boolean HWFLOW;
    private static final String TAG = "WifiChannelXmlParse";
    private static final String WIFI_CHANNEL_CFG = "wifiChannel";
    private static final String WIFI_CHANNEL_CFG_ROOT = "wifi_channel_config";
    private static WifiChannelXmlParse mWifiChannelXmlParse = null;
    private HashMap<String, WifiChannelCfg> mChannelsInfoFor2G = new HashMap();
    private HashMap<String, WifiChannelCfg> mChannelsInfoFor5G = new HashMap();

    private class WifiChannelCfg {
        String bandName;
        boolean enabled;
        boolean is2G;
        ArrayList<Integer> values = new ArrayList();

        public WifiChannelCfg(String bandName, boolean enabled, String value) {
            this.bandName = bandName;
            this.enabled = enabled;
            int i = 0;
            this.is2G = false;
            String[] valueStr = value.split(",");
            int lenght = valueStr.length;
            while (i < lenght) {
                if ("A".equals(valueStr[i])) {
                    this.values.add(Integer.valueOf(36));
                    this.values.add(Integer.valueOf(38));
                    this.values.add(Integer.valueOf(40));
                    this.values.add(Integer.valueOf(42));
                    this.values.add(Integer.valueOf(44));
                    this.values.add(Integer.valueOf(46));
                    this.values.add(Integer.valueOf(48));
                } else if ("B".equals(valueStr[i])) {
                    this.values.add(Integer.valueOf(149));
                    this.values.add(Integer.valueOf(151));
                    this.values.add(Integer.valueOf(153));
                    this.values.add(Integer.valueOf(155));
                    this.values.add(Integer.valueOf(157));
                    this.values.add(Integer.valueOf(159));
                    this.values.add(Integer.valueOf(161));
                    this.values.add(Integer.valueOf(165));
                } else if ("C".equals(valueStr[i])) {
                    this.values.add(Integer.valueOf(153));
                    this.values.add(Integer.valueOf(155));
                    this.values.add(Integer.valueOf(157));
                    this.values.add(Integer.valueOf(159));
                    this.values.add(Integer.valueOf(161));
                    this.values.add(Integer.valueOf(165));
                } else if (Integer.valueOf(valueStr[i]).intValue() >= 1 && Integer.valueOf(valueStr[i]).intValue() <= 14) {
                    this.is2G = true;
                    this.values.add(Integer.valueOf(valueStr[i]));
                } else if (Integer.valueOf(valueStr[i]).intValue() >= 34 && Integer.valueOf(valueStr[i]).intValue() <= 189) {
                    this.values.add(Integer.valueOf(valueStr[i]));
                }
                i++;
            }
        }

        String getBandName() {
            return this.bandName;
        }

        ArrayList<Integer> getValues() {
            return this.values;
        }

        boolean is2G() {
            return this.is2G;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("bandName: ");
            stringBuilder.append(this.bandName);
            sb.append(stringBuilder.toString());
            stringBuilder = new StringBuilder();
            stringBuilder.append(", enabled ");
            stringBuilder.append(this.enabled);
            sb.append(stringBuilder.toString());
            sb.append(", values: ");
            int lenght = this.values.size();
            for (int i = 0; i < lenght; i++) {
                sb.append(((Integer) this.values.get(i)).toString());
                sb.append(",");
            }
            return sb.toString();
        }
    }

    static {
        boolean z = Log.HWINFO || (Log.HWModuleLog && Log.isLoggable(TAG, 4));
        HWFLOW = z;
    }

    private WifiChannelXmlParse() {
        try {
            pasreConfigFile(DEFAULT_CONFIG_NAME);
            if (DBG) {
                traverseChannels(this.mChannelsInfoFor2G);
                traverseChannels(this.mChannelsInfoFor5G);
            }
            pasreConfigFile(CUST_CONFIG_NAME);
            if (DBG) {
                traverseChannels(this.mChannelsInfoFor2G);
                traverseChannels(this.mChannelsInfoFor5G);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static WifiChannelXmlParse getInstance() {
        if (mWifiChannelXmlParse == null) {
            mWifiChannelXmlParse = new WifiChannelXmlParse();
        }
        return mWifiChannelXmlParse;
    }

    private void pasreConfigFile(String fileName) throws IOException {
        InputStream inputStream = null;
        File configFile = HwCfgFilePolicy.getCfgFile(fileName, 0);
        if (configFile == null || !configFile.exists()) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("file ");
            stringBuilder.append(fileName);
            stringBuilder.append(" not find");
            Log.e(str, stringBuilder.toString());
            return;
        }
        try {
            inputStream = new FileInputStream(configFile);
            if (DEFAULT_CONFIG_NAME.equals(fileName)) {
                pasreConfigFileImpl(inputStream, false);
            } else {
                pasreConfigFileImpl(inputStream, true);
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (XmlPullParserException e3) {
            e3.printStackTrace();
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Exception e4) {
            e4.printStackTrace();
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
        }
    }

    private void pasreConfigFileImpl(InputStream inputStream, boolean overlay) throws XmlPullParserException, IOException {
        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setInput(inputStream, "UTF-8");
        int event = pullParser.getEventType();
        while (true) {
            boolean enabled = true;
            if (event != 1) {
                if (event != 0) {
                    switch (event) {
                        case 2:
                            if (!WIFI_CHANNEL_CFG.equals(pullParser.getName())) {
                                break;
                            }
                            String name = pullParser.getAttributeValue(0);
                            if (1 != Integer.valueOf(pullParser.getAttributeValue(1)).intValue()) {
                                enabled = false;
                            }
                            WifiChannelCfg wifiChannelCfg = new WifiChannelCfg(name, enabled, pullParser.getAttributeValue(2));
                            if (DBG) {
                                String str = TAG;
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("");
                                stringBuilder.append(wifiChannelCfg.toString());
                                Log.d(str, stringBuilder.toString());
                            }
                            if (!enabled) {
                                if (overlay) {
                                    if (!wifiChannelCfg.is2G()) {
                                        this.mChannelsInfoFor5G.remove(name);
                                        break;
                                    } else {
                                        this.mChannelsInfoFor2G.remove(name);
                                        break;
                                    }
                                }
                                break;
                            } else if (!wifiChannelCfg.is2G()) {
                                this.mChannelsInfoFor5G.put(name, wifiChannelCfg);
                                break;
                            } else {
                                this.mChannelsInfoFor2G.put(name, wifiChannelCfg);
                                break;
                            }
                        default:
                            break;
                    }
                } else if (DBG) {
                    Log.d(TAG, "START_DOCUMENT");
                }
                event = pullParser.next();
            } else {
                return;
            }
        }
    }

    public ArrayList<Integer> getValidChannels(String bandName, boolean is2G) {
        String str;
        StringBuilder stringBuilder;
        WifiChannelCfg wifiChannelCfg = (WifiChannelCfg) (is2G ? this.mChannelsInfoFor2G : this.mChannelsInfoFor5G).get(bandName);
        if (DBG) {
            str = TAG;
            stringBuilder = new StringBuilder();
            stringBuilder.append("getValidChannels ");
            stringBuilder.append(bandName);
            Log.d(str, stringBuilder.toString());
        }
        if (wifiChannelCfg == null) {
            return null;
        }
        if (DBG) {
            str = TAG;
            stringBuilder = new StringBuilder();
            stringBuilder.append("getValidChannels got.");
            stringBuilder.append(wifiChannelCfg.toString());
            Log.d(str, stringBuilder.toString());
        }
        return wifiChannelCfg.getValues();
    }

    private void traverseChannels(HashMap<String, WifiChannelCfg> channelsInfo) {
        Log.d(TAG, "channelsInfo : ");
        for (Entry entry : channelsInfo.entrySet()) {
            WifiChannelCfg valueCfg = (WifiChannelCfg) entry.getValue();
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(valueCfg.toString());
            Log.i(str, stringBuilder.toString());
        }
        Log.d(TAG, ":::end");
    }
}
