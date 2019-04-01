import { Injectable } from '@angular/core';
import { Customer } from './customer';

@Injectable({
  providedIn: 'root'
})
export class SessionService {

  constructor() { }

  getIsLogin(): boolean {
    if (sessionStorage.isLogin == "true") {
      return true;
    }
    else {
      return false;
    }
  }

  setIsLogin(isLogin: boolean): void {
    sessionStorage.isLogin = isLogin;
  }


  getCurrentCustomer(): Customer {
    return JSON.parse(sessionStorage.currentCustomer);
  }


  setCurrentCustomer(currentCustomer: Customer): void {
    sessionStorage.currentCustomer = JSON.stringify(currentCustomer);
  }

}
