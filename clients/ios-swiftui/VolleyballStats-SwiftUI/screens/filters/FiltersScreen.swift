//
//  FiltersView.swift
//  VolleyballStats-SwiftUI
//
//  Created by Kamil Halko on 02/01/2024.
//

import Foundation
import SwiftUI
import shared

struct FiltersScreen: View {
    
    private let scope: Scope
    private let filtersPresenter: FiltersPresenter
    
    init(presentersFactory: PresentersFactory, screen: ScreenFilters) {
        self.scope = presentersFactory.createScope()
        self.filtersPresenter = presentersFactory.createFiltersPresenter(scope: scope, screen: screen)
    }
    
    var body: some View {
        Color.gray
    }
}
