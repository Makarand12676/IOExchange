package com.mak.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.mak.flows.utilities.QueryUtilities;
import com.mak.states.StockState;
import com.r3.corda.lib.tokens.contracts.types.TokenPointer;
import com.r3.corda.lib.tokens.contracts.types.TokenType;
import com.r3.corda.lib.tokens.money.FiatCurrency;
import com.r3.corda.lib.tokens.workflows.utilities.QueryUtilitiesKt;
import net.corda.core.contracts.Amount;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.utilities.ProgressTracker;

public class QueryStock {

    @InitiatingFlow
    @StartableByRPC
    public static class GetStockBalance extends FlowLogic<String> {
        private final ProgressTracker progressTracker = new ProgressTracker();
        private final String symbol;

        public GetStockBalance(String symbol) {
            this.symbol = symbol;
        }

        @Override
        public ProgressTracker getProgressTracker() {
            return progressTracker;
        }

        @Override
        @Suspendable
        public String call() throws FlowException {
            TokenPointer<StockState> stockPointer = QueryUtilities.queryStockPointer(symbol, getServiceHub());
            Amount<TokenType> amount = QueryUtilitiesKt.tokenBalance(getServiceHub().getVaultService(), stockPointer);
            return "\n You currently have "+ amount.getQuantity()+ " " +this.symbol + " stocks\n";
        }
    }


    @InitiatingFlow
    @StartableByRPC
    public static class GetFiatBalance extends FlowLogic<Amount<TokenType>> {
        private final ProgressTracker progressTracker = new ProgressTracker();
        private final String currencyCode;

        public GetFiatBalance(String currencyCode) {
            this.currencyCode = currencyCode;
        }

        @Override
        public ProgressTracker getProgressTracker() {
            return progressTracker;
        }

        @Override
        @Suspendable
        public Amount<TokenType> call() throws FlowException {
            TokenType fiatTokenType = FiatCurrency.Companion.getInstance(currencyCode);
            Amount<TokenType> amount = QueryUtilitiesKt.tokenBalance(getServiceHub().getVaultService(), fiatTokenType);
            return amount;
        }
    }

}

