/* tslint:disable no-unused-expression */
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { BankAccountComponentsPage, BankAccountDeleteDialog, BankAccountUpdatePage } from './bank-account.page-object';

const expect = chai.expect;

describe('BankAccount e2e test', () => {
    let navBarPage: NavBarPage;
    let signInPage: SignInPage;
    let bankAccountUpdatePage: BankAccountUpdatePage;
    let bankAccountComponentsPage: BankAccountComponentsPage;
    /*let bankAccountDeleteDialog: BankAccountDeleteDialog;*/

    before(async () => {
        await browser.get('/');
        navBarPage = new NavBarPage();
        signInPage = await navBarPage.getSignInPage();
        await signInPage.autoSignInUsing('admin', 'admin');
        await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
    });

    it('should load BankAccounts', async () => {
        await navBarPage.goToEntity('bank-account');
        bankAccountComponentsPage = new BankAccountComponentsPage();
        expect(await bankAccountComponentsPage.getTitle()).to.eq('Bank Accounts');
    });

    it('should load create BankAccount page', async () => {
        await bankAccountComponentsPage.clickOnCreateButton();
        bankAccountUpdatePage = new BankAccountUpdatePage();
        expect(await bankAccountUpdatePage.getPageTitle()).to.eq('Create or edit a Bank Account');
        await bankAccountUpdatePage.cancel();
    });

    /* it('should create and save BankAccounts', async () => {
        const nbButtonsBeforeCreate = await bankAccountComponentsPage.countDeleteButtons();

        await bankAccountComponentsPage.clickOnCreateButton();
        await promise.all([
            bankAccountUpdatePage.setAccountNameInput('accountName'),
            bankAccountUpdatePage.setAccountBankInput('accountBank'),
            bankAccountUpdatePage.setInitialAmountInput('5'),
            bankAccountUpdatePage.accountSelectLastOption(),
        ]);
        expect(await bankAccountUpdatePage.getAccountNameInput()).to.eq('accountName');
        expect(await bankAccountUpdatePage.getAccountBankInput()).to.eq('accountBank');
        expect(await bankAccountUpdatePage.getInitialAmountInput()).to.eq('5');
        await bankAccountUpdatePage.save();
        expect(await bankAccountUpdatePage.getSaveButton().isPresent()).to.be.false;

        expect(await bankAccountComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
    });*/

    /* it('should delete last BankAccount', async () => {
        const nbButtonsBeforeDelete = await bankAccountComponentsPage.countDeleteButtons();
        await bankAccountComponentsPage.clickOnLastDeleteButton();

        bankAccountDeleteDialog = new BankAccountDeleteDialog();
        expect(await bankAccountDeleteDialog.getDialogTitle())
            .to.eq('Are you sure you want to delete this Bank Account?');
        await bankAccountDeleteDialog.clickOnConfirmButton();

        expect(await bankAccountComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
    });*/

    after(async () => {
        await navBarPage.autoSignOut();
    });
});
