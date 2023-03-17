package com.solvd.solvdtestingproxy;

import com.browserup.bup.BrowserUpProxy;
import com.browserup.bup.proxy.CaptureType;
import com.browserup.harreader.model.Har;
import com.browserup.harreader.model.HarEntry;
import com.browserup.harreader.model.HarRequest;
import com.qaprosoft.carina.core.foundation.IAbstractTest;
import com.zebrunner.carina.core.registrar.ownership.MethodOwner;
import com.zebrunner.carina.proxy.browserup.ProxyPool;
import com.zebrunner.carina.utils.R;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class SolvdProxyTest implements IAbstractTest {

    BrowserUpProxy proxy;

    @BeforeMethod(alwaysRun = true)
    public void startProxy() {
        R.CONFIG.put("browserup_proxy", "true", true);
        R.CONFIG.put("proxy_type", "DYNAMIC", true);
        R.CONFIG.put("proxy_port", "0", true);
        getDriver();
        proxy = ProxyPool.getProxy();
        proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
        proxy.newHar();
    }

    @Test()
    @MethodOwner(owner = "qpsdemo")
    public void callContainsNonEmptyDataTest() {
        SoftAssert softAssert = new SoftAssert();

        HomePage homePage = new HomePage(getDriver());
        homePage.open();

        Har har = proxy.getHar();
        for (HarEntry entry : har.getLog().getEntries()) {
            HarRequest request = entry.getRequest();

            softAssert.assertTrue(request.getUrl().contains("https://rest.happierleads.com"), "Current session doesn't contain request to https://rest.happierleads.com");
            softAssert.assertTrue(request.getMethod().name().contains("POST"), "Current session doesn't contain POST request to https://rest.happierleads.com");

            if(request.getUrl().contains("https://rest.happierleads.com") && request.getMethod().name().contains("POST")) {
                JSONObject requestBody = new JSONObject(request.getPostData().getText());
                softAssert.assertNotNull(requestBody.getString("data"), "Request body contains empty 'data' property.");
            }
        }
    }
}