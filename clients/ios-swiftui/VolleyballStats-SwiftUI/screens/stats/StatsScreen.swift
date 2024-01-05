//
//  StatsScreen.swift
//  VolleyballStats-SwiftUI
//
//  Created by Kamil Halko on 02/01/2024.
//

import Foundation
import SwiftUI
import shared

struct StatsScreen: View {
    
    private let scope: Scope
    private let presenter: StatsPresenter
    
    init(presentersFactory: PresentersFactory, screen: ScreenStats) {
        self.scope = presentersFactory.createScope()
        self.presenter = presentersFactory.createStatsPresenter(scope: scope, screen: screen)
    }
    
    var body: some View {
        Screen(
            stateFlow: presenter.state,
            scope: scope,
            onFabButtonClicked: { presenter.state.value.onFabButtonClicked() }
        ) { state in
            Spacer()
        }
    }
}
