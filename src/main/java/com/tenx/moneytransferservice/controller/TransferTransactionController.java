package com.tenx.moneytransferservice.controller;

import com.tenx.moneytransferservice.exception.AccountNotFoundException;
import com.tenx.moneytransferservice.exception.CurrencyMismatchException;
import com.tenx.moneytransferservice.exception.InsufficientBalanceException;
import com.tenx.moneytransferservice.exception.TransferBetweenSameAccountException;
import com.tenx.moneytransferservice.model.Account;
import com.tenx.moneytransferservice.model.TransferTransaction;
import com.tenx.moneytransferservice.service.AccountService;
import com.tenx.moneytransferservice.service.TransferTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/v1/transfers")
public class TransferTransactionController {
    @Autowired
    AccountService accountService;
    @Autowired
    TransferTransactionService transferTransactionService;

    private static final Logger logger = LoggerFactory.getLogger(TransferTransactionController.class);
    /**
     *
     * creates money transfer transaction
     * @param transferTransactionDTO
     *      {
     *     "sourceAccountId": "102",
     *     "targetAccountId": "101",
     *     "amount": "100"
     *     }
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity createMoneyTransferTransaction(@RequestBody TransferTransactionDTO transferTransactionDTO) throws URISyntaxException {
        Account sourceAccount = accountService.findAccount(transferTransactionDTO.getSourceAccountId());
        if(sourceAccount.getId().equals(transferTransactionDTO.getTargetAccountId())){
            throw new TransferBetweenSameAccountException("Transfer between same account is not possible : " + sourceAccount.getId());
        }
        Account targetAccount = accountService.findAccount(transferTransactionDTO.getTargetAccountId());
        if(sourceAccount.getBalance().compareTo(transferTransactionDTO.getAmount()) < 0){
            throw new InsufficientBalanceException("Insufficent Balance For : " + sourceAccount.getId());
        }
        if(sourceAccount.getCurrency() != targetAccount.getCurrency()){
            throw new CurrencyMismatchException("Source and Target account has currency mismatch. Source Currency : " + sourceAccount.getCurrency() + "" +
                    " Target Currency : " + targetAccount.getCurrency() );
        }
        TransferTransaction transaction = transferTransactionService.transferMoney(sourceAccount, targetAccount, transferTransactionDTO.getAmount() );
        return ResponseEntity
            .created(new URI("/v1/transfers/" + transaction.getId())).build();
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ResponseDTO> returnNotFound(AccountNotFoundException e) {
        logger.error("Account not found {}",e.getMessage());
        return new ResponseEntity<ResponseDTO>(ResponseDTO.builder().message(e.getMessage()).build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            InsufficientBalanceException.class,
            TransferBetweenSameAccountException.class,
            CurrencyMismatchException.class
    })
    public ResponseEntity<ResponseDTO> returnNotAcceptable(RuntimeException e) {
        logger.error("Validation rules are not match {}",e.getMessage());
        return new ResponseEntity<ResponseDTO>(ResponseDTO.builder().message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ResponseDTO> returnConcurrentAccountModify(ObjectOptimisticLockingFailureException e) {
        logger.error("Concurrent Modify exceptions has occurred {} {}","Transfer failed, There is a modify operation inprogress on account, please try again",e.getMessage());
        logger.error("Exception occurred {}",e.getMessage());
        return new ResponseEntity<ResponseDTO>(ResponseDTO.builder().message("Transfer failed, There is a modify operation inprogress on account, please try again").build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO> returnInternalServerError(Exception e) {
        logger.error("Exception has occurred {}",e.getMessage());
        return new ResponseEntity<ResponseDTO>(ResponseDTO.builder().message(e.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
