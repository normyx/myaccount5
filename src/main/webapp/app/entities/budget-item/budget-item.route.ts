import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { BudgetItem } from 'app/shared/model/budget-item.model';
import { BudgetItemService } from './budget-item.service';
import { BudgetItemComponent } from './budget-item.component';
import { BudgetItemDetailComponent } from './budget-item-detail.component';
import { BudgetItemUpdateComponent } from './budget-item-update.component';
import { BudgetItemCreateComponent } from './budget-item-create.component';
import { BudgetItemDeletePopupComponent } from './budget-item-delete-dialog.component';
import { IBudgetItem } from 'app/shared/model/budget-item.model';

@Injectable({ providedIn: 'root' })
export class BudgetItemResolve implements Resolve<IBudgetItem> {
    constructor(private service: BudgetItemService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(map((budgetItem: HttpResponse<BudgetItem>) => budgetItem.body));
        }
        return of(new BudgetItem());
    }
}

export const budgetItemRoute: Routes = [
    {
        path: 'budget-item',
        component: BudgetItemComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'BudgetItems'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'budget-item/:id/view',
        component: BudgetItemDetailComponent,
        resolve: {
            budgetItem: BudgetItemResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'BudgetItems'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'budget-item/:id/edit',
        component: BudgetItemUpdateComponent,
        resolve: {
            budgetItem: BudgetItemResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'BudgetItems'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'budget-item/new',
        component: BudgetItemUpdateComponent,
        resolve: {
            budgetItem: BudgetItemResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'BudgetItems'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'budget-item/new-with-budget-item-periods',
        component: BudgetItemCreateComponent,
        resolve: {
            budgetItem: BudgetItemResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'BudgetItems'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const budgetItemPopupRoute: Routes = [
    {
        path: 'budget-item/:id/delete',
        component: BudgetItemDeletePopupComponent,
        resolve: {
            budgetItem: BudgetItemResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'BudgetItems'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
