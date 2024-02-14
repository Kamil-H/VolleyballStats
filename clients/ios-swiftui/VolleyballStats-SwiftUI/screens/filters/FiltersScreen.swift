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
            ScrollView {
                var spinnerSelection: Binding<Int> {
                    Binding(
                        get: { Int(state.segmentedControlState.selectedIndex) },
                        set: { presenter.onControlItemSelected(selectedIndex: Int32($0)) }
                    )
                }
                VStack {
                    Picker("", selection: spinnerSelection) {
                        ForEach(Array(state.segmentedControlState.items.enumerated()), id: \.element) { index, element in
                            Text(element).tag(index)
                        }
                    }
                    .pickerStyle(.segmented)
                    Spacer(minLength: Dimens.marginMedium)
                    switch state.showControl {
                    case .filters:
                        VStack(spacing: Dimens.marginLarge) {
                            SelectOptionView(selectOption: state.seasonSelectOption)
                            Divider()
                            SelectOptionView(selectOption: state.specializationSelectOption)
                            Divider()
                            SelectOptionView(selectOption: state.teamsSelectOption)
                        }
                    case .properties:
                        ChooseProperties(choosePropertiesState: state.properties)
                    }
                }.padding(.vertical, Dimens.marginMedium)
            }
        }
    }
}
