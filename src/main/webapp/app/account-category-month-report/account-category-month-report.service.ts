import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
// import { IAccountCategoryMonthReport } from './account-category-month-report.model';

type EntityResponseType = HttpResponse<any>;

@Injectable({ providedIn: 'root' })
export class AccountCategoryMonthReportService {
    private resourceUrl = SERVER_API_URL + 'api/report-monthly-data';

    constructor(private http: HttpClient) {}

    getData(accountId: number, categoryId: number): Observable<EntityResponseType> {
        return this.http.get<any>(`${this.resourceUrl}/${accountId}/${categoryId}`, { observe: 'response' });
    }
}
