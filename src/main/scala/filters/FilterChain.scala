package filters

import traits.Filter

class FilterChain(filters: List[Filter]) {
  def applyFilters(table: List[List[String]]): List[List[String]] = {
    filters.foldLeft(table)((filteredTable, filter) => filter.filter(filteredTable))
  }
}
