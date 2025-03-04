package com.vultisig.wallet.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vultisig.wallet.R
import com.vultisig.wallet.common.asString
import com.vultisig.wallet.ui.components.FormCard
import com.vultisig.wallet.ui.components.MultiColorButton
import com.vultisig.wallet.ui.components.UiAlertDialog
import com.vultisig.wallet.ui.components.UiBarContainer
import com.vultisig.wallet.ui.components.UiHorizontalDivider
import com.vultisig.wallet.ui.components.UiLinearProgressIndicator
import com.vultisig.wallet.ui.components.UiSpacer
import com.vultisig.wallet.ui.components.library.UiCheckbox
import com.vultisig.wallet.ui.models.TransactionUiModel
import com.vultisig.wallet.ui.models.VerifyTransactionUiModel
import com.vultisig.wallet.ui.models.VerifyTransactionViewModel
import com.vultisig.wallet.ui.theme.Theme

@Composable
internal fun VerifyTransactionScreen(
    navController: NavHostController,
    viewModel: VerifyTransactionViewModel = hiltViewModel(),
) {
    val state = viewModel.uiState.collectAsState().value

    val errorText = state.errorText
    if (errorText != null) {
        UiAlertDialog(
            title = stringResource(id = R.string.dialog_default_error_title),
            text = errorText.asString(),
            onDismiss = viewModel::dismissError,
        )
    }

    UiBarContainer(
        title = stringResource(R.string.verify_transaction_screen_title),
        navController = navController,
    ) {
        VerifyTransactionScreen(
            state = state,
            isProgressEnabled = true,
            isConsentsEnabled = true,
            confirmTitle = stringResource(R.string.verify_transaction_sign),
            onConsentAddress = viewModel::checkConsentAddress,
            onConsentAmount = viewModel::checkConsentAmount,
            onConsentDst = viewModel::checkConsentDst,
            onConfirm = viewModel::joinKeysign,
        )
    }
}

@Composable
internal fun VerifyTransactionScreen(
    state: VerifyTransactionUiModel,
    isProgressEnabled: Boolean,
    isConsentsEnabled: Boolean,
    confirmTitle: String,
    onConfirm: () -> Unit,
    onConsentAddress: (Boolean) -> Unit = {},
    onConsentAmount: (Boolean) -> Unit = {},
    onConsentDst: (Boolean) -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(all = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            if (isProgressEnabled) {
                UiLinearProgressIndicator(
                    progress = 0.6f,
                )

                // size 0 but still adds margin because of verticalArrangement
                UiSpacer(size = 0.dp)
            }

            FormCard {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .padding(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        )
                ) {
                    AddressField(
                        title = stringResource(R.string.verify_transaction_from_title),
                        address = state.transaction.srcAddress
                    )

                    AddressField(
                        title = stringResource(R.string.verify_transaction_to_title),
                        address = state.transaction.dstAddress
                    )

                    OtherField(
                        title = stringResource(R.string.verify_transaction_amount_title),
                        value = state.transaction.tokenValue,
                    )

                    OtherField(
                        title = stringResource(
                            R.string.verify_transaction_fiat_amount_title,
                            state.transaction.fiatCurrency
                        ),
                        value = state.transaction.fiatValue,
                    )

                    OtherField(
                        title = stringResource(R.string.verify_transaction_gas_title),
                        value = state.transaction.gasValue,
                        divider = false
                    )
                }
            }

            if (isConsentsEnabled) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    CheckField(
                        title = stringResource(R.string.verify_transaction_consent_address),
                        isChecked = state.consentAddress,
                        onCheckedChange = onConsentAddress,
                    )

                    CheckField(
                        title = stringResource(R.string.verify_transaction_consent_amount),
                        isChecked = state.consentAmount,
                        onCheckedChange = onConsentAmount,
                    )

                    CheckField(
                        title = stringResource(R.string.verify_transaction_consent_correct_dst),
                        isChecked = state.consentDst,
                        onCheckedChange = onConsentDst,
                    )
                }
            }
        }

        MultiColorButton(
            text = confirmTitle,
            textColor = Theme.colors.oxfordBlue800,
            minHeight = 44.dp,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(all = 16.dp),
            onClick = onConfirm,
        )
    }
}

@Composable
private fun AddressField(
    title: String,
    address: String,
) {
    Column {
        Text(
            text = title,
            color = Theme.colors.neutral100,
            style = Theme.montserrat.heading5,
        )

        UiSpacer(size = 16.dp)

        Text(
            text = address,
            style = Theme.montserrat.titleSmall,
            color = Theme.colors.turquoise800,
        )

        UiSpacer(size = 12.dp)

        UiHorizontalDivider()
    }
}

@Composable
private fun OtherField(
    title: String,
    value: String,
    divider: Boolean = true,
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                vertical = 12.dp,
            )
        ) {
            Text(
                text = title,
                color = Theme.colors.neutral100,
                style = Theme.montserrat.subtitle1,
            )

            UiSpacer(weight = 1f)
            UiSpacer(size = 8.dp)

            Text(
                text = value,
                textAlign = TextAlign.End,
                color = Theme.colors.neutral100,
                style = Theme.menlo.subtitle1,
            )
        }

        if (divider) {
            UiHorizontalDivider()
        }
    }
}

@Composable
private fun CheckField(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 4.dp,
                vertical = 8.dp,
            ),
    ) {
        UiCheckbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )

        UiSpacer(size = 8.dp)

        Text(
            text = title,
            color = Theme.colors.neutral100,
            style = Theme.menlo.body2,
        )
    }
}

@Preview
@Composable
private fun VerifyTransactionScreenPreview() {
    VerifyTransactionScreen(
        state = VerifyTransactionUiModel(
            transaction = TransactionUiModel(
                srcAddress = "0x1234567890",
                dstAddress = "0x1234567890",
                tokenValue = "1.1",
                fiatValue = "1.1",
                fiatCurrency = "USD",
                gasValue = "1.1",
            )
        ),
        isProgressEnabled = true,
        isConsentsEnabled = true,
        confirmTitle = stringResource(R.string.verify_transaction_sign),
        onConsentAddress = {},
        onConsentAmount = {},
        onConsentDst = {},
        onConfirm = {},
    )
}