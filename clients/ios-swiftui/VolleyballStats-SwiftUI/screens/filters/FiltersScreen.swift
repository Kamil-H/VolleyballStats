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
    private let presenter: FiltersPresenter
    
    init(presentersFactory: PresentersFactory, screen: ScreenFilters) {
        self.scope = presentersFactory.createScope()
        self.presenter = presentersFactory.createFiltersPresenter(scope: scope, screen: screen)
    }
    
    var body: some View {
        Screen(
            stateFlow: presenter.state,
            scope: scope
        ) { state in
            Spacer()
        }
    }
}
