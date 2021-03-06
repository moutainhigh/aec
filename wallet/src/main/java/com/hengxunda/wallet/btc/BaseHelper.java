package com.hengxunda.wallet.btc;



import com.hengxunda.common.exception.BusinessException;
import com.hengxunda.common.exception.ServerException;
import com.hengxunda.common.utils.A;
import com.hengxunda.wallet.coin.bitcoin.jsonrpcclient.Bitcoin;
import com.hengxunda.wallet.coin.bitcoin.jsonrpcclient.BitcoinJSONRPCClient;
import com.hengxunda.wallet.coin.bitcoin.jsonrpcclient.BitcoinRPCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;

public class BaseHelper {

    private static Logger logger = LoggerFactory.getLogger(BaseHelper.class);

    private BitcoinJSONRPCClient client;
    private String coinName;

    public BaseHelper(String username, String password,
                      String host, String port,
                      String coinName) {
        this.coinName = coinName;

        try {
            URL url = new URL("http://" + username + ':' + password + "@"
                    + (host == null ? "127.0.0.1" : host) + ":" + (port == null ? "8332" : port + "/"));
            client = new BitcoinJSONRPCClient(url);
            logger.info("======== create {} JSON-RPC-CLIENT[host:{}, port:{}, username:{}, password:{}] ========", coinName, host, port, username, password);
        } catch (Exception e) {
            logger.error("节点配置信息错误，错误原因：" + e.getMessage(), e);
        }
    }

    /**
     * 获取地址
     * @param account 标签
     * @return
     */
    public String getAccountAddress(String account) {
        try {
            return this.client.getAccountAddress(account);
        } catch (Exception e) {
            throw new ServerException("创建" + this.coinName + "账户错误，错误原因：" + e.getMessage(), e);
        }
    }


    /**
     * 获取余额
     * @param account 地址
     * @return
     */
    public double getBalance(String account) {
        try {
            return this.client.getBalance(account);
        } catch (Exception e) {
            throw new ServerException("获取" + this.coinName + "余额错误，错误原因：" + e.getMessage(), e);
        }
    }


    /**
     * 获取钱包服务总余额
     * @return double
     */
    public double getBalance() {
        try {
            return this.client.getBalance();
        } catch (Exception e) {
            throw new ServerException("获取钱包服务总额错误，错误原因：" + e.getMessage(), e);
        }
    }


    public String sendFrom(String fromAccount, String toAddress, double amount) {
        try {
            A.check(!this.client.validateAddress(fromAccount).isValid(), "转出钱包地址错误");
            A.check(!this.client.validateAddress(toAddress).isValid(), "目标钱包地址错误");
            return this.client.sendFrom(fromAccount, toAddress, amount);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            checkError(e);
            throw new ServerException(this.coinName + "sendFrom[" + fromAccount + ", " + toAddress + ", " + amount + "]错误，错误原因：" + e.getMessage(), e);
        }
    }

    public String sendToAddress(String toAddress, double amount) {

        try {
            A.check(!this.client.validateAddress(toAddress).isValid(), "目标钱包地址错误");
            return this.client.sendToAddress(toAddress, amount);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            checkError(e);
            throw new ServerException(this.coinName + "sendToAddress[" + toAddress + ", " + amount + "]错误，错误原因：" + e.getMessage(), e);
        }
    }

    public Bitcoin.RawTransaction getTransaction(String transactionId) {
        try {
            return this.client.getTransaction(transactionId);
        } catch (Exception e) {
            LinkedHashMap error = this.getBitcoinError(e);
            if(error != null) {
                return null;
            }
            throw new ServerException("获取" + this.coinName + "交易错误，错误原因：" + e.getMessage(), e);
        }
    }

    /**
     * 获取最新的块
     * @return
     */
    public int getBlockCount() {
        try {
            return this.client.getBlockCount();
        } catch (Exception e) {
            throw new ServerException("获取" + this.coinName + ":BlockCount错误，错误原因：" + e.getMessage(), e);
        }
    }

    public Bitcoin.Block getBlock(int blockId) {
        try {
            return this.client.getBlock(this.client.getBlockHash(blockId));
        } catch (Exception e) {
            throw new ServerException("获取" + this.coinName + ":Block[" + blockId + "]错误，错误原因：" + e.getMessage(), e);
        }
    }

    public List<String> getTransactionHashesByBlock(int blockId) {
        try {
            String blockHash = this.client.getBlockHash(blockId);
            if(blockHash == null) {
                return null;
            }
            Bitcoin.Block block = this.client.getBlock(blockHash);
            if(block == null) {
                return null;
            }

            return block.tx();
        } catch (Exception e) {
            throw new ServerException("获取" + this.coinName + ":getTransactionHashesByBlock[" + blockId + "]错误，错误原因：" + e.getMessage(), e);
        }
    }

    private void checkError(Exception e) {
        LinkedHashMap error = this.getBitcoinError(e);

        if(error == null) {
            return;
        }

        long code = error.get("code") == null ? Long.MAX_VALUE : (Long)error.get("code");
        if(code == -1L) {
            logger.error(e.getMessage(), e);
            throw new BusinessException(-2, "金额错误");
        }
        if(code == -6L) {
            logger.error(e.getMessage(), e);
            throw new BusinessException(-1, "余额不足");
        }
    }

    private LinkedHashMap getBitcoinError(Exception e) {
        if(!(e instanceof BitcoinRPCException)) {
            return null;
        }

        BitcoinRPCException rpcException = (BitcoinRPCException)e;
        if(rpcException.getResponse() == null || rpcException.getResponse().get("error") == null) {
            return null;
        }

        return (LinkedHashMap)rpcException.getResponse().get("error");
    }

//    public static void main(String[] args) throws Exception {
//        /*URL url = new URL("http://user_1qazWSX:pass_1qazWSX@172.31.18.195:18377/");
//        System.out.println(url.getPath());*/
//        BaseHelper btcBaseHelper = new BaseHelper("wqt_user","wqt_pass","47.92.73.26","18776","btc");
//        System.out.println(btcBaseHelper.getBlockCount());
//    }
}
