//
//  MainViewModel.swift
//  VolleyballStats-SwiftUI
//
//  Created by Kamil Halko on 22/12/2023.
//

import SwiftUI
import shared


@MainActor class MainViewModel: ObservableObject {
    
    private let mainPresenter: MainPresenter
    
    @Published
    private(set) var state: MainState
    
    init(mainPresenter: MainPresenter) {
        self.mainPresenter = mainPresenter
        state = mainPresenter.state.value
    }
    
    @MainActor
    func activate() async {
        for await state in mainPresenter.state {
            self.state = state
        }
    }
}
