import { Component, OnInit, Input } from '@angular/core';

import { Product } from '../product';
import { ActivatedRoute } from '@angular/router';
import { DataService } from '../data.service';
import { ProductService } from '../product.service';

@Component({
  selector: 'app-view-product-details',
  templateUrl: './view-product-details.component.html',
  styleUrls: ['./view-product-details.component.css'],
})
export class ViewProductDetailsComponent implements OnInit {

  private product: Product;
  private quantity: number = 0;
  private errorMessage: string;

  constructor(private activatedRoute: ActivatedRoute,
    private productService: ProductService) { }

  ngOnInit() {
    let productId = parseInt(this.activatedRoute.snapshot.paramMap.get('productId'));

    this.productService.getProductId(productId).subscribe(response =>
      this.product = response.productEntity
    ),
      (error: string) => {
        this.errorMessage = error;
      }
  }

  format() {
    if (this.product != null) {
      return (new Intl.NumberFormat('en-SG', { style: 'currency', currency: 'SGD' }).format(this.product.unitPrice));
    }
  }

  add() {
    if (this.quantity < 10 && this.quantity < this.product.quantityOnHand) {
      this.quantity = this.quantity + 1;
    }
  }

  minus() {
    if (this.quantity > 0) {
      this.quantity = this.quantity - 1;
    }
  }

  addToCart() {
    console.log("Adding to cart!");
    console.log(this.product.reviewEntities)
  }
}
