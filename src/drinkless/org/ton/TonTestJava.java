package drinkless.org.ton;

import java.util.concurrent.CountDownLatch;
import drinkless.org.ton.Client;
import drinkless.org.ton.TonApi;

public class TonTestJava {
    static class JavaClient {
        Client client = Client.create(null, null, null);

        public Object send(TonApi.Function query) {
            Object[] result = new Object[1];
            CountDownLatch countDownLatch = new CountDownLatch(1);

            class Callback implements Client.ResultHandler {
                Object[] result;
                CountDownLatch countDownLatch;

                Callback(Object[] result, CountDownLatch countDownLatch) {
                    this.result = result;
                    this.countDownLatch = countDownLatch;
                }

                public void onResult(TonApi.Object object) {
                    if (object instanceof TonApi.Error) {
                        appendLog(((TonApi.Error) object).message);
                    } else {
                        result[0] = object;
                    }
                    if (countDownLatch != null) {
                        countDownLatch.countDown();
                    }
                }
            }

            client.send(query, new Callback(result, countDownLatch) , null);
            if (countDownLatch != null) {
                try {
                    countDownLatch.await();
                } catch (Throwable e) {
                    appendLog(e.toString());
                }
            }
            return result[0];
        }
    }

    private static void appendLog(String log) {
        System.out.println(log);
    }

    public static void main(String[] args) {
        appendLog("start...");
        String[] words = {
                "project",
                "planet",
                "betray",
                "brief",
                "coral",
                "dizzy",
                "melody",
                "pepper",
                "mandate",
                "better",
                "bar",
                "like",
                "lock",
                "reveal",
                "gas",
                "hunt",
                "ghost",
                "fringe",
                "soap",
                "term",
                "robust",
                "urge",
                "fortune",
                "good"
        };
        String dir = "."; // set your directory to storage tonlib data
        JavaClient client = new JavaClient();
        Object result = client.send(new TonApi.Init(new TonApi.Options(new TonApi.Config(GlobalConfig.config, "", false, false), new TonApi.KeyStoreTypeDirectory((dir)))));
        if (!(result instanceof TonApi.OptionsInfo)) {
            appendLog("failed to set config");
            return;
        }
        appendLog("config set ok");
        TonApi.OptionsInfo info = (TonApi.OptionsInfo)result;
        TonApi.Key key = (TonApi.Key) client.send(new TonApi.CreateNewKey("local password".getBytes(), "mnemonic password".getBytes(), "".getBytes()));
        TonApi.InputKey inputKey = new TonApi.InputKeyRegular(key, "local password".getBytes());
        TonApi.AccountAddress walletAddress = (TonApi.AccountAddress)client.send(new TonApi.GetAccountAddress(new TonApi.WalletV3InitialAccountState(key.publicKey, info.configInfo.defaultWalletId), 1, 0));

        TonApi.Key giverKey = (TonApi.Key)client.send(new TonApi.ImportKey("local password".getBytes(), "".getBytes(), new TonApi.ExportedKey(words))) ;
        TonApi.InputKey giverInputKey = new TonApi.InputKeyRegular(giverKey, "local password".getBytes());
        TonApi.AccountAddress giverAddress = (TonApi.AccountAddress)client.send(new TonApi.GetAccountAddress(new TonApi.WalletV3InitialAccountState(giverKey.publicKey, info.configInfo.defaultWalletId), 1, 0));

        appendLog("sending coins...");
        TonApi.QueryInfo queryInfo = (TonApi.QueryInfo)client.send(new TonApi.CreateQuery(giverInputKey, giverAddress, 60, new TonApi.ActionMsg(new TonApi.MsgMessage[]{new TonApi.MsgMessage(walletAddress, "", 6660000000L, new TonApi.MsgDataText("Hello".getBytes()) )}, true), new TonApi.WalletV3InitialAccountState(giverKey.publicKey, info.configInfo.defaultWalletId)));
        result = client.send(new TonApi.QuerySend(queryInfo.id));
        if (!(result instanceof TonApi.Ok)) {
            appendLog("failed to send coins");
            return;
        }
        appendLog("coins sent, getting balance");

        while (true) {
            TonApi.FullAccountState state = (TonApi.FullAccountState) client.send(new TonApi.GetAccountState(walletAddress));
            if (state.balance <= 0L) {
                try {
                    Thread.sleep(1000);
                } catch (Throwable e) {
                    appendLog(e.toString());
                }
            } else {
                appendLog(String.format("balance = %d", state.balance));
                break;
            }
        }
    }
}