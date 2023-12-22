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
    private let navigationEventReceiver: NavigationEventReceiver
    
    @Published
    private(set) var state: MainState
    
    @Published
    private(set) var navigationEvent: NavigationEvent? = nil
    
    init(mainPresenter: MainPresenter, navigationEventReceiver: NavigationEventReceiver) {
        self.mainPresenter = mainPresenter
        self.navigationEventReceiver = navigationEventReceiver
        state = mainPresenter.state.value
    }
    
    @MainActor
    func activate() async {
        async let _ = listenState()
        async let _ = listenNavigationEvent()
    }
    
    private func listenState() async {
        for await state in mainPresenter.state {
            self.state = state
        }
    }
    
    private func listenNavigationEvent() async {
        for await navigationEvent in navigationEventReceiver.receive() {
            self.navigationEvent = navigationEvent
        }
    }
}

