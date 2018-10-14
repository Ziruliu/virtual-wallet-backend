package com.ziruliu.backend;

import com.ziruliu.backend.controller.WalletController;
import com.ziruliu.backend.entity.Transaction;
import com.ziruliu.backend.exception.NegativeAccountBalanceException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.runner.RunWith;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(WalletController.class)
public class VirtualWalletBackendApplicationTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private WalletController walletController;

    private SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Test the '/user/wallet' end point, a message will be returned upon success
     * @throws Exception
     */
	@Test
	public void createWallet() throws Exception {

	    given(walletController.createWallet()).willReturn(Collections.singletonMap("response", "Wallet successfully created!"));

	    mockMvc.perform(get("/user/wallet"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("response").value("Wallet successfully created!"));

	}

    /**
     * Test the '/user/balance' end point, and current account balance should be 0
     * @throws Exception
     */
    @Test
    public void getBalance() throws Exception {

        given(walletController.getBalance()).willReturn(Collections.singletonMap("account balance", new BigDecimal(0)));

        mockMvc.perform(get("/user/balance"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("['account balance']").value(new BigDecimal(0)));

    }

    /**
     * Test the '/user/deposit/{amount}' end point, a deposit transaction with an amount of 100 should be returned
     * @throws Exception
     */
    @Test
    public void deposit() throws Exception {

        given(walletController.deposit(new BigDecimal(100))).willReturn(new Transaction("Deposit", new BigDecimal(100), localDateFormat.format(new Date())));

        mockMvc.perform(get("/user/deposit/100"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("type").value("Deposit"))
                .andExpect(jsonPath("amount").value(new BigDecimal(100)));

    }

    /**
     * The second test of the '/user/balance' end point, and the current account balance should be 100
     * @throws Exception
     */
    @Test
    public void getBalance_2() throws Exception {

        given(walletController.getBalance()).willReturn(Collections.singletonMap("account balance", new BigDecimal(100)));

        mockMvc.perform(get("/user/balance"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("['account balance']").value(new BigDecimal(100)));

    }

    /**
     * Test the '/user/withdrawal/{amount}' end point, a withdrawal amount of 200 should throw NegativeAccountBalanceException
     * @throws Exception
     */
    @Test
    public void withdrawal() throws Exception {

        given(walletController.withdrawal(new BigDecimal(200))).willThrow(new NegativeAccountBalanceException("Withdrawal amount cannot exceed account balance"));

        mockMvc.perform(get("/user/withdrawal/200"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("message").value("Withdrawal amount cannot exceed account balance"));

    }

    /**
     * The second test of the '/user/withdrawal/{amount}' end point, a withdrawal transaction with an amount of 50 should be returned
     * @throws Exception
     */
    @Test
    public void withdrawal_2() throws Exception {

        given(walletController.withdrawal(new BigDecimal(50))).willReturn(new Transaction("Withdrawal", new BigDecimal(50), localDateFormat.format(new Date())));

        mockMvc.perform(get("/user/withdrawal/50"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("type").value("Withdrawal"))
                .andExpect(jsonPath("amount").value(new BigDecimal(50)));
    }

    /**
     * The third test of the '/user/balance' end point, and current account balance should be 50
     * @throws Exception
     */
    @Test
    public void getBalance_3() throws Exception {

        given(walletController.getBalance()).willReturn(Collections.singletonMap("account balance", new BigDecimal(50)));

        mockMvc.perform(get("/user/balance"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("['account balance']").value(new BigDecimal(50)));

    }

    /**
     * Test the '/user/history/{N}' end point, and the last 2 transactions should be returned upon success
     * @throws Exception
     */
    @Test
    public void get_last_N_transactions() throws Exception {

        List<Transaction> expected_list = new ArrayList<>();

        expected_list.add(new Transaction("Withdrawal", new BigDecimal(50), localDateFormat.format(new Date())));
        expected_list.add(new Transaction("Deposit", new BigDecimal(100), localDateFormat.format(new Date())));

        given(walletController.get_last_N_transactions(2)).willReturn(expected_list);

        mockMvc.perform(get("/user/history/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].type").value("Withdrawal"))
                .andExpect(jsonPath("$.[0].amount").value(new BigDecimal(50)))
                .andExpect(jsonPath("$.[1].type").value("Deposit"))
                .andExpect(jsonPath("$.[1].amount").value(new BigDecimal(100)));
    }

}
