import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'filterCorrect',
  standalone: true
})
export class FilterCorrectPipe implements PipeTransform {
  transform(items: any[] | null, property: string): number {
    if (!items) return 0;
    return items.filter(item => item[property]).length;
  }
}
