import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IBudgetItem } from 'app/shared/model/budget-item.model';
import { ICategory } from 'app/shared/model/category.model';
import { Principal } from 'app/core';
import { MyaBudgetItemService } from './mya-budget-item.service';
import { CategoryService } from 'app/entities/category/category.service';
import * as Moment from 'moment';
import 'moment/locale/fr';

@Component({
    selector: 'jhi-mya-budget-item',
    templateUrl: './mya-budget-item.component.html'
})
export class MyaBudgetItemComponent implements OnInit, OnDestroy {
    readonly NUMBER_OF_MONTHS_TO_DISPLAY: number = 6;
    budgetItems: IBudgetItem[];
    currentAccount: any;
    eventSubscriber: Subscription;
    selectedMonth: Date;
    monthsToDisplay: Date[];
    filterCategories: ICategory[];
    filterSelectedCategory: ICategory;
    filterContains: string;

    constructor(
        private budgetItemService: MyaBudgetItemService,
        private categoryService: CategoryService,
        private jhiAlertService: JhiAlertService,
        private eventManager: JhiEventManager,
        private principal: Principal
    ) {
        // get the current time
        const current: Date = new Date(Date.now());
        // set the selected date to this month
        this.selectedMonth = new Date(current.getFullYear(), current.getMonth(), 1);
        this.resetMonthsToDisplay();
    }

    resetMonthsToDisplay() {
        // set the NUMBER_OF_MONTHS_TO_DISPLAY elements of monthsToDisplay
        const mtd = new Array(this.NUMBER_OF_MONTHS_TO_DISPLAY);
        let i: number;
        for (i = 0; i < this.NUMBER_OF_MONTHS_TO_DISPLAY; i++) {
            mtd[i] = new Date(this.selectedMonth.getFullYear(), this.selectedMonth.getMonth() + i, 1);
        }
        this.monthsToDisplay = mtd;
    }

    handleSelectMonth(event) {
        this.resetMonthsToDisplay();
        this.eventManager.broadcast({ name: 'myaBudgetItemListModification', content: 'OK' });
    }

    loadAll() {
        /*const criteria = {
            'month.greaterOrEqualThan': Moment(this.monthsToDisplay[0]).format('YYYY-MM-DD'),
            'month.lessOrEqualThan': Moment(this.monthsToDisplay[this.monthsToDisplay.length - 1]).format('YYYY-MM-DD')
        };*/
        this.budgetItemService
            .findEligible(
                Moment(this.monthsToDisplay[0]).format('YYYY-MM-DD'),
                Moment(this.monthsToDisplay[this.monthsToDisplay.length - 1]).format('YYYY-MM-DD')
            )
            .subscribe(
                (res: HttpResponse<IBudgetItem[]>) => {
                    this.budgetItems = res.body;
                },
                (res: HttpErrorResponse) => this.onError(res.message)
            );
    }

    ngOnInit() {
        this.categoryService.query().subscribe(
            (res: HttpResponse<ICategory[]>) => {
                this.filterCategories = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
        this.loadAll();
        this.principal.identity().then(account => {
            this.currentAccount = account;
        });
        this.registerChangeInBudgetItems();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: IBudgetItem) {
        return item.id;
    }

    registerChangeInBudgetItems() {
        this.eventSubscriber = this.eventManager.subscribe('myaBudgetItemListModification', response => this.loadAll());
    }

    private onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}