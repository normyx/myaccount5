import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IBudgetItem } from 'app/shared/model/budget-item.model';
import { IBudgetItemPeriod } from 'app/shared/model/budget-item-period.model';
import { BudgetItemService } from 'app/entities/budget-item/budget-item.service';
import * as Moment from 'moment';
import 'moment/locale/fr';

type EntityResponseType = HttpResponse<IBudgetItem>;
type EntityArrayResponseType = HttpResponse<IBudgetItem[]>;

@Injectable({ providedIn: 'root' })
export class MyaBudgetItemService {
    private resourceUrl = SERVER_API_URL + 'api/budget-items';
    private resourceAvailableUrl = SERVER_API_URL + 'api/budget-eligible-items';
    private resourceExtendUrl = SERVER_API_URL + 'api/extend-budget-item-periods-and-next';
    private resourceLastBudgetItemPeriodUrl = SERVER_API_URL + 'api/last-budget-item-period';
    private resourceWithBudgetItemPeriodUrl = SERVER_API_URL + 'api/budget-items-with-periods';
    private resourceUpUrl = SERVER_API_URL + 'api/budget-item-up-order';
    private resourceDownUrl = SERVER_API_URL + 'api/budget-item-down-order';

    constructor(private http: HttpClient, private budgetItemService: BudgetItemService) {}

    /*create(budgetItem: IBudgetItem): Observable<EntityResponseType> {
        return this.budgetItemService.create(budgetItem);
    }*/

    createWithBudgetItemPeriods(
        budgetItem: IBudgetItem,
        isSmoothed: boolean,
        monthFrom: Date,
        amount: number,
        dayOfMonth: number
    ): Observable<EntityResponseType> {
        const monthStr = Moment(monthFrom).format('YYYY-MM-DD');
        return this.http.post<IBudgetItem>(
            `${this.resourceWithBudgetItemPeriodUrl}/${isSmoothed}/${monthStr}/${amount}/${dayOfMonth}`,
            budgetItem,
            { observe: 'response' }
        );
    }

    update(budgetItem: IBudgetItem): Observable<EntityResponseType> {
        return this.budgetItemService.update(budgetItem);
    }

    find(id: number): Observable<EntityResponseType> {
        return this.budgetItemService.find(id);
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        return this.budgetItemService.query(req);
    }

    deleteWithPeriods(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceWithBudgetItemPeriodUrl}/${id}`, { observe: 'response' });
    }

    findEligible(from: string, to: string, contains: string, categoryId: number): Observable<EntityArrayResponseType> {
        let params = '?';
        if (contains) {
            params = params + 'contains=' + contains + '&';
        }
        if (categoryId) {
            params = params + 'categoryId=' + '' + categoryId;
        }
        return this.http.get<IBudgetItem[]>(`${this.resourceAvailableUrl}/${from}/${to}${params}`, { observe: 'response' });
    }

    extend(id: number): Observable<HttpResponse<void>> {
        return this.http.post<any>(`${this.resourceExtendUrl}/${id}`, { observe: 'response' });
    }

    lastBudgetItem(id: number): Observable<HttpResponse<IBudgetItemPeriod>> {
        return this.http.get<IBudgetItemPeriod>(`${this.resourceLastBudgetItemPeriodUrl}/${id}`, { observe: 'response' });
    }

    up(bipId: number): Observable<HttpResponse<void>> {
        return this.http.get<any>(`${this.resourceUpUrl}/${bipId}`, { observe: 'response' });
    }
    down(bipId: number): Observable<HttpResponse<void>> {
        return this.http.get<any>(`${this.resourceDownUrl}/${bipId}`, { observe: 'response' });
    }
}
