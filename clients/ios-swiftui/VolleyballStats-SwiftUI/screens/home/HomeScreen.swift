//
//  HomeScreen.swift
//  VolleyballStats-SwiftUI
//
//  Created by Kamil Halko on 21/12/2023.
//

import SwiftUI
import shared

struct HomeScreen: View {
    
    private let scope: Scope
    private let presenter: HomePresenter
    
    init(presentersFactory: PresentersFactory) {
        self.scope = presentersFactory.createScope()
        self.presenter = presentersFactory.createHomePresenter(scope: scope)
    }
    
    var body: some View {
        Screen(
            stateFlow: presenter.state,
            scope: scope,
            onActionButtonClicked: { presenter.onRetry() },
            onMessageButtonClicked: { presenter.onRetry() },
            onMessageDismissed: { presenter.onMessageDismissed() }
        ) { state in
            MatchList(groupedMatchItems: state.matches)
        }
    }
}
