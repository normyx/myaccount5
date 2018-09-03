import { browser, ExpectedConditions as ec } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { SubCategoryComponentsPage, SubCategoryDeleteDialog, SubCategoryUpdatePage } from './sub-category.page-object';

describe('SubCategory e2e test', () => {
    let navBarPage: NavBarPage;
    let signInPage: SignInPage;
    let subCategoryUpdatePage: SubCategoryUpdatePage;
    let subCategoryComponentsPage: SubCategoryComponentsPage;
    let subCategoryDeleteDialog: SubCategoryDeleteDialog;

    beforeAll(async () => {
        await browser.get('/');
        navBarPage = new NavBarPage();
        signInPage = await navBarPage.getSignInPage();
        await signInPage.autoSignInUsing('admin', 'admin');
        await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
    });

    it('should load SubCategories', async () => {
        await navBarPage.goToEntity('sub-category');
        subCategoryComponentsPage = new SubCategoryComponentsPage();
        expect(await subCategoryComponentsPage.getTitle()).toMatch(/Sub Categories/);
    });

    it('should load create SubCategory page', async () => {
        await subCategoryComponentsPage.clickOnCreateButton();
        subCategoryUpdatePage = new SubCategoryUpdatePage();
        expect(await subCategoryUpdatePage.getPageTitle()).toMatch(/Create or edit a Sub Category/);
        await subCategoryUpdatePage.cancel();
    });

    it('should create and save SubCategories', async () => {
        await subCategoryComponentsPage.clickOnCreateButton();
        await subCategoryUpdatePage.setSubCategoryNameInput('subCategoryName');
        expect(await subCategoryUpdatePage.getSubCategoryNameInput()).toMatch('subCategoryName');
        await subCategoryUpdatePage.categorySelectLastOption();
        await subCategoryUpdatePage.save();
        expect(await subCategoryUpdatePage.getSaveButton().isPresent()).toBeFalsy();
    });

    it('should delete last SubCategory', async () => {
        const nbButtonsBeforeDelete = await subCategoryComponentsPage.countDeleteButtons();
        await subCategoryComponentsPage.clickOnLastDeleteButton();

        subCategoryDeleteDialog = new SubCategoryDeleteDialog();
        expect(await subCategoryDeleteDialog.getDialogTitle()).toMatch(/Are you sure you want to delete this Sub Category?/);
        await subCategoryDeleteDialog.clickOnConfirmButton();

        expect(await subCategoryComponentsPage.countDeleteButtons()).toBe(nbButtonsBeforeDelete - 1);
    });

    afterAll(async () => {
        await navBarPage.autoSignOut();
    });
});
